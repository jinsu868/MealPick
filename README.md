
<p align="center">
  <img src="https://github.com/user-attachments/assets/d2a3060a-7a83-4549-b787-1fb8826de2e5" width="600">
</p>

<p align="center">
  실시간 음식 랭킹, 음식 저장 및 공유 SNS 서비스
</p>

## 프로젝트 소개
실시간으로 다른 사람이 어떤 음식을 먹고 있는지, 그리고 내가 먹은 음식을 다른 사람과 공유할 수 있습니다.
팔로우/팔로잉 기능을 제공하며 다른 사람과 실시간 채팅이 가능합니다.


## 시스템 아키텍처

<img width="957" alt="2025-03-08_17-40-58" src="https://github.com/user-attachments/assets/3bf37a8a-bea1-42b7-af31-1490b8da68fe" />

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


## 기여한 부분

### 음식 랭킹 기능 구현
![rank](https://github.com/user-attachments/assets/a6728cb4-e953-42af-9f78-c54825fe2398)

실시간으로 다른 사람이 어떤 음식을 많이 먹고 있는지 확인할 수 있습니다.
WebSocket과 Redis의 SortedSet 자료형을 활용하여 실시간 태그 랭킹 정보를 보여줍니다. 

#### 플로우
<img width="691" alt="2025-03-08_18-03-44" src="https://github.com/user-attachments/assets/c2b49276-fdee-465d-bb4b-7757f7b8314e" />

게시글을 등록하면 Redis의 SortedSet value의 태그들의 score를 증가시키고 Redis pub/sub을 통해 모든 서버에 변경된 랭킹 정보를 브로드캐스팅합니다.

#### 코드
<img width="724" alt="2025-03-08_18-09-39" src="https://github.com/user-attachments/assets/9097e213-5af4-417c-bf70-f295a0d72c8c" />

처음에는 subscriber에서 각각 Redis에서 조회해서 응답해주는 구조였는데 publisher에서 한 번 조회해서 넘겨주는 방식으로 변경했습니다.




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

임시저장

## 🎉 밀픽 스태프를 소개합니다

|BackEnd|BackEnd|BackEnd|
|:-:|:-:|:-:|
|<img src="https://avatars.githubusercontent.com/u/130902228?v=4" width="100" height="100">|<img src="https://avatars.githubusercontent.com/u/102043957?v=4" width="100" height="100">|<img src="https://avatars.githubusercontent.com/u/139448668?v=4" width="100" height="100">|
|<a href="https://lab.ssafy.com/s12-webmobile1-sub1/S12P11A803/-/commits/dev-BE?author=%EB%B0%95%EC%A2%85%ED%95%98" title="Code">작업 내용 💻</a>|<a href="https://lab.ssafy.com/s12-webmobile1-sub1/S12P11A803/-/commits/dev-BE?author=%EB%AC%B8%EC%A7%84%EC%88%98" title="Code">작업 내용 💻</a>|<a href="https://lab.ssafy.com/s12-webmobile1-sub1/S12P11A803/-/commits/dev-BE?author=%EC%9D%B4%EC%A3%BC%ED%98%B8" title="Code">작업 내용 💻</a>|
|[박종하](https://github.com/freeftr)|[문진수](https://github.com/jinsu868)|[이주호](https://github.com/lsc713)|

|FrontEnd|FrontEnd|FrontEnd|
|:-:|:-:|:-:|
|<img src="https://avatars.githubusercontent.com/u/110987711?v=4" width="100" height="100">|<img src="https://avatars.githubusercontent.com/u/175118763?v=4" width="100" height="100">|<img src="https://avatars.githubusercontent.com/u/81206228?v=4" width="100" height="100">|
|<a href="https://lab.ssafy.com/s12-webmobile1-sub1/S12P11A803/-/commits/dev-FE?author=%EC%B5%9C%EC%A4%80%ED%98%81" title="Code">작업 내용 💻</a>|<a href="https://lab.ssafy.com/s12-webmobile1-sub1/S12P11A803/-/commits/dev-FE?author=%EB%B0%95%EC%A7%84%EC%88%98" title="Code">작업 내용 💻</a>|<a href="https://lab.ssafy.com/s12-webmobile1-sub1/S12P11A803/-/commits/dev-FE?author=%EC%97%AC%ED%98%84%EC%8A%B9" title="Code">작업 내용 💻</a>|
|[최준혁](https://github.com/raonrabbit)|[박진수](https://github.com/Jim-bu)|[여현승](https://github.com/hyvnsevng)|


