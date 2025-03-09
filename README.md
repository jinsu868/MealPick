
<p align="center">
  <img src="https://github.com/user-attachments/assets/d2a3060a-7a83-4549-b787-1fb8826de2e5" width="600">
</p>

<p align="center">
  실시간 음식 랭킹, 음식 저장 및 공유 SNS 서비스
</p>

## 프로젝트 소개
실시간으로 다른 사람이 어떤 음식을 먹고 있는지, 그리고 내가 먹은 음식을 다른 사람과 공유할 수 있습니다.
팔로우/팔로잉 기능을 제공하며 다른 사람과 채팅이 가능합니다.


## 시스템 아키텍처

<img width="1283" alt="2025-03-09_14-19-43" src="https://github.com/user-attachments/assets/7b91315a-9493-418e-a067-0341fb571a9a" />


## 기여한 부분

### 음식 랭킹 기능 구현
![rank](https://github.com/user-attachments/assets/a6728cb4-e953-42af-9f78-c54825fe2398)

실시간으로 다른 사람이 어떤 음식을 많이 먹고 있는지 확인할 수 있습니다.
WebSocket과 Redis의 SortedSet 자료형을 활용하여 실시간 태그 랭킹 정보를 보여줍니다. 

### 게시글 생성 시 태그 음식 갱신

<img width="772" alt="2025-03-08_19-09-05" src="https://github.com/user-attachments/assets/40349bf5-a519-4d10-89ba-d596671247f7" />

### 코드
<img width="724" alt="2025-03-08_18-09-39" src="https://github.com/user-attachments/assets/9097e213-5af4-417c-bf70-f295a0d72c8c" />

처음에는 subscriber에서 각각 조회해서 응답해주는 구조였는데 publisher에서 한 번 조회해서 넘겨주는 방식으로 변경했습니다.

### 실시간 채팅 기능 구현

WebSocket 기술을 활용하여 실시간 채팅 기능을 구현했습니다.

### 전체 플로우
<img width="996" alt="2025-03-08_20-29-27" src="https://github.com/user-attachments/assets/d993fb2e-f133-47c6-acd3-b4c8e7ca8ca1" />

### 코드
<img width="788" alt="2025-03-08_20-26-43" src="https://github.com/user-attachments/assets/333edcba-4e9d-4365-9265-a3973b05971b" />



현재 Consumer에서 Relay 전파를 할 때 별도의 스레드 풀을 생성하고 각 스레드에 RestTemplate으로 동기 I/O 방식으로 처리하고 있습니다.
이 경우 요청의 개수가 많아지면 스레드가 부족해지고 처리가 지연될 수 있습니다.

**추후 개선 방안) WebClient를 사용하여 I/O Multiplexing으로 처리하면 더 적은 스레드 수로 처리량을 높일 수 있습니다.**

채팅 관련 아키텍처 고민은 아래 링크에서 확인할 수 있습니다.
[https://github.com/jinsu868/MealPick/issues/3](https://github.com/jinsu868/MealPick/issues/3)

## 프로젝트 기술 스택

### Backend
<div align="center">
  <h3> 기술 스택 </h3>
  <img src="https://img.shields.io/badge/Java17-000000?style=flat-square&logo=java&color=F40D12">
  <img src="https://img.shields.io/badge/Spring_Boot_3-0?style=flat-square&logo=spring-boot&logoColor=white&color=%236DB33F">
  <img src="https://img.shields.io/badge/MySQL_8-0?style=flat-square&logo=mysql&logoColor=white&color=4479A1">
  <img src="https://img.shields.io/badge/Nginx-0?style=flat-square&logo=nginx&logoColor=white&color=009639">
  <img src="https://img.shields.io/badge/Hibernate-0?style=flat-square&logo=hibernate&logoColor=white&color=%2359666C">
  <br/>
  <img src="https://img.shields.io/badge/Amazon_EC2-0?style=flat-square&logo=amazon-ec2&logoColor=white&color=%23FF9900">
  <img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=flat-square&logo=apache-kafka&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white">
  <br/>
  <img src="https://img.shields.io/badge/OAuth2-0?style=flat-square&logo=oauth2&logoColor=white&color=%23000000">
  <img src="https://img.shields.io/badge/Gradle-0?style=flat-square&logo=gradle&logoColor=white&color=%2302303A">
  <img src="https://img.shields.io/badge/JUnit5-0?style=JUnit5-square&logo=junit5&logoColor=white&color=%2325A162">
  <img src="https://img.shields.io/badge/Jenkins-0?style=flat-square&logo=Jenkins&logoColor=white&color=%23D24939">
  <br/>
  <img src="https://img.shields.io/badge/GitLab-FC6D26?style=flat-square&logo=gitlab&logoColor=white">
  <img src="https://img.shields.io/badge/Jira-0052CC?style=flat-square&logo=jira&logoColor=white">
  <img src="https://img.shields.io/badge/REST_Docs-6DB33F?style=flat-square&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white">
</div>
<br/>
<br/>

### Frontend
- DataBinding, MVVM 
- Tailwind React Redux WebSocket PWA
- Firebase Analytics FCM, Kakao sdk 


