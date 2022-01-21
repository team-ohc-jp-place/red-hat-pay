package rhpay.loadtest;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JFRParser {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_TIME;

    public static void main(String[] args) throws Exception {
        final String targetPathStr = args[0];
        final String outputFilenameStr = args[1];

        final Path targetPath = Paths.get(targetPathStr);

        List<Path> targetJfrFileList = null;
        if (Files.isDirectory(targetPath)) {
            // ディレクトリが指定された場合

            targetJfrFileList = Files.list(targetPath).filter(p -> p.getFileName().toString().endsWith(".jfr")).collect(Collectors.toUnmodifiableList());
            System.out.println(String.format("ディレクトリ [%s] が指定されました", targetPath.toAbsolutePath().toString()));

        } else {
            // ファイルが指定された場合
            targetJfrFileList = List.of(targetPath);
        }

        if (targetJfrFileList.size() == 0) {
            System.out.println("パースするJFRファイルが見つかりません");
            return;
        }

        System.out.println(String.format("JFRファイル %d 個をパースします", targetJfrFileList.size()));
        targetJfrFileList.stream().forEach(System.out::println);

        Map<String, ParseParameter> parseSettingMap = createParseSetting();

        boolean hasError = false;
        Map<String, Path> outputFileMap = new HashMap<>(parseSettingMap.size());

        for (Map.Entry<String, ParseParameter> parseSetting : parseSettingMap.entrySet()) {

            final Path outputFilename = Paths.get(outputFilenameStr + "_" + parseSetting.getValue().filePrefix + ".csv");

            // 出力先の確認
            if (Files.isDirectory(outputFilename)) {
                System.err.println(String.format("指定された出力先 [%s] はすでにディレクトリとして存在します", outputFilename.toAbsolutePath().toString()));
            } else if (Files.exists(outputFilename)) {
                System.err.println(String.format("指定された出力先 [%s] はすでにファイルとして存在します", outputFilename.toAbsolutePath().toString()));
            }

            outputFileMap.put(parseSetting.getKey(), outputFilename);
        }

        if (hasError) {
            throw new RuntimeException("出力先ファイルと同名のファイルがあるため処理を続けられませんでした");
        }

        Map<String, BufferedWriter> writerMap = new HashMap<>(parseSettingMap.size());
        for (Map.Entry<String, Path> outputFilePath : outputFileMap.entrySet()) {
            BufferedWriter writer = Files.newBufferedWriter(outputFilePath.getValue());
            // ヘッダの書き込み
            writer.write(parseSettingMap.get(outputFilePath.getKey()).dataHeader);
            writer.newLine();
            writerMap.put(outputFilePath.getKey(), writer);
        }

        try {
            // データの書き込み
            for (Path jfrFile : targetJfrFileList) {
                try (RecordingFile recodingFile = new RecordingFile((jfrFile))) {

                    while (recodingFile.hasMoreEvents()) {

                        RecordedEvent event = recodingFile.readEvent();
                        String eventType = event.getEventType().getName();

                        // 記録対象のイベントなら書き込む
                        if (parseSettingMap.containsKey(eventType)) {
                            String line = parseSettingMap.get(eventType).body.apply(event);
                            BufferedWriter writer = writerMap.get(eventType);
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                    System.out.println(String.format("INFO  : ファイル [%s] のパースが完了しました", jfrFile.getFileName().toString()));
                } catch (Exception e) {
                    System.err.println(String.format("ERROR : ファイル [%s] のパースに失敗しました", jfrFile.getFileName().toString()));
                    e.printStackTrace();
                }
            }
        } finally {
            for (Map.Entry<String, BufferedWriter> writerEntry : writerMap.entrySet()) {
                try {
                    BufferedWriter writer = writerEntry.getValue();
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static final Map<String, ParseParameter> createParseSetting() {
        Map<String, ParseParameter> parseSettingMap = new HashMap<>(4);
        parseSettingMap.put("rhpay.monitoring.event.TaskEvent", new ParseParameter(
                "ServerTask",
                "StartTime(sec),EndTime(sec),Duration(ns),ThreadName,ShopperId,TokenId",
                e -> String.format("%s,%s,%d,%s,%d,%s", dateFormat(e.getStartTime()), dateFormat(e.getEndTime()), e.getDuration().getNano(), e.getThread().getJavaName(), e.getInt("shopperId"), e.getString("tokenId"))
        ));

        parseSettingMap.put("rhpay.monolith.monitoring.RestEvent", new ParseParameter(
                "MonolithRest",
                "StartTime(sec),EndTime(sec),Duration(ns),Method",
                e -> String.format("%s,%s,%d,%s", dateFormat(e.getStartTime()), dateFormat(e.getEndTime()), e.getDuration().getNano(), e.getString("method"))
        ));

        parseSettingMap.put("rhpay.monitoring.event.DistributedTaskEvent", new ParseParameter(
                "DistributedTask",
                "StartTime(sec),EndTime(sec),Duration(ns),ThreadName,TaskName,ShopperId,TokenId,TryCount",
                e -> String.format("%s,%s,%d,%s,%s,%d,%s,%d", dateFormat(e.getStartTime()), dateFormat(e.getEndTime()), e.getDuration().getNano(), e.getThread().getJavaName(), e.getString("taskName"), e.getInt("shopperId"), e.getString("tokenId"), e.getInt("tryCount"))
        ));

        parseSettingMap.put("rhpay.monitoring.TokenRestEvent", new ParseParameter(
                "DGRest",
                "StartTime(sec),EndTime(sec),Duration(ns),Method,ShopperId,TokenId",
                e -> String.format("%s,%s,%d,%s,%d,%s", dateFormat(e.getStartTime()), dateFormat(e.getEndTime()), e.getDuration().getNano(), e.getString("method"), e.getInt("shopperId"), e.getString("tokenId"))
        ));

        return parseSettingMap;
    }

    private final static String dateFormat(Instant time) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(time.getEpochSecond(), 0, ZoneOffset.of("+09:00"));
        return dateFormatter.format(ldt);
    }
}

class ParseParameter {
    public final String filePrefix;
    public final String dataHeader;
    public final Function<RecordedEvent, String> body;

    public ParseParameter(String filePrefix, String dataHeader, Function<RecordedEvent, String> body) {
        this.filePrefix = filePrefix;
        this.dataHeader = dataHeader;
        this.body = body;
    }
}
