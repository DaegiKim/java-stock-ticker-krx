# java-stock-ticker-krx
Java 기반의 실시간 주가 표시 어플리케이션 (한국거래소 전용)

Real-time stock tickers from the command-line based on Java



![java-stock-ticker-kr](https://raw.githubusercontent.com/DaegiKim/java-stock-ticker-krx/master/screenshot.gif)

## Requirements
- Java 10+
- Dependencies
  - commons-cli:commons-cli:1.4
  - org.apache.httpcomponents:httpclient:4.5.9
  - org.json:json:20180813
  - org.jsoup:jsoup:1.11.3

## Usage
First, build the project to generate the jar file.
```sh
# Single symbol with refresh interval 250 millis:
# 예) 단일 코드, 새로고침 간격 0.25초:
$ java -jar ./java-stock-ticker-krx.jar -i 250 -s 207760  
 
# Multiple symbols with refresh interval 250 millis:
# 예) 다중 코드, 새로고침 간격 0.25초:
$ java -jar ./java-stock-ticker-krx.jar -i 250 -s 100030 101730 105550 115440 115450
```

#### Note
This app only supports stocks registered on the Korea Exchange(krx). This app uses the Naver Finance API.

#
*Inspired by [ticker.sh](https://github.com/pstadler/ticker.sh)*
