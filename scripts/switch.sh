#!/bin/bash

# Crawl current connected port of WAS

CURRENT_PORT=$(cat /home/ubuntu/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Nginx currently proxies to ${CURRENT_PORT}."

# Toggle port number
if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081
else
  echo "> No WAS is connected to nginx"
  exit 1
fi

# Change proxying port into target port
# tee 는
  # 출력 내용을 파일로 만들어주는 커맨드입니다.
  # 새로 띄운 WAS의 포트를 nginx가 읽을 수 있도록 service_url.inc에 내용을 덮어씁니다.
echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | tee /home/ubuntu/service_url.inc

echo "> Now Nginx proxies to ${TARGET_PORT}."

# Reload nginx (nginx 서버의 재시작 없이 바로 새로운 설정값으로 서비스를 이어나갈 수 있도록 합니다.)
sudo service nginx reload

echo "> Nginx reloaded."
