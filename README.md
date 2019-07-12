# java-stock-ticker-krx
Real-time stock tickers from the command-line based on Java

![java-stock-ticker-kr](https://raw.githubusercontent.com/DaegiKim/java-stock-ticker-krx/master/screenshot.gif)

## Requirements
- Java 10+
- Dependencies
  - org.apache.httpcomponents:httpclient:4.5.9
  - org.json:json:20180813

## Usage
First, build the project to generate the jar file.
```sh
# Single symbol with refresh interval 250 millis:
$ java -jar ./java-stock-ticker-krx.jar 250 207760  
 
# Multiple symbols with refresh interval 250 millis:
$ java -jar ./java-stock-ticker-krx.jar 250 100030 101730 105550 115440 115450
```

#### Note
This app only supports stocks registered on the Korea Exchange(krx). This app uses the Naver Finance API.

#
*Inspired by [ticker.sh](https://github.com/pstadler/ticker.sh)*