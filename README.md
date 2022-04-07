![image](https://user-images.githubusercontent.com/97423483/161703547-7a2814f4-2c42-4856-94be-005cb7fe061d.png)


# We-Write (위라이트)
## 모두와 함께 완성하는 릴레이 소설 플랫폼 ‘위라이트' 입니다!
[위라이트 바로가기](https://wewrite.co.kr)



<br/>

## 위라이트 팀

---

BE(Spring) [🔰홍하빈](https://github.com/binscot)   
BE(Spring) [정택규](https://github.com/JeongTaekgyu)  
BE(Spring) [정희재](https://github.com/fnzl08)  

FE(React) [🔰유동건](https://github.com/peppermintt0504)  
FE(React) [소정현](https://github.com/sojh93)  
FE(React) [조현준](https://github.com/johj703)  

UX/UI 유효진  
UX/UI 김소연

<br/>

---
 
<br/>
안녕하세요!<br/>
저희 ‘위라이트'는 카테고리를 정해 첫문단을 작성해 다른 유저들과 함께 릴레이 형식으로 소설을 이어 써서 완성할 수 있는 서비스입니다.<br/>타인과 함께 완성하는만큼 소설이 어떤 완결로 나아갈진 아무도 몰라요.
<br/>흥미진진한 릴레이, 나의 필력을 뽐내보아요!  

1. ⭐첫 작성자가 소설의 문단 수를 정하고 첫번째 문단을 작성 하면 이어서 문장들을 작성해 나갈 수 있어요!
2. 🙅‍♀️ 먼저 작성중인 문단은 끼어들 수 없어요!
3. 🚩 정해진 문단 수가 다 차게 되면 소설은 완성됩니다!!
4. 👍 소설에 좋아요와 북마크는 물론, 최고의 문장에 좋아요 를 누를 수 있어요!
5. 👑 사이트를 지속적으로 활동하며 초보 작가에서 프로 작가로 계정을 성장시킬 수 있어요!



<br/>


##📆 프로젝트 기간

---


2022년 2월 25일 ~ 2022년 4월 6일  
<br/>
2월 25일 - 3월 2일 : 기획  
3월 3일 - 3월 10일 : 프로젝트 구상 및 설계  
3월 11일 - 3월 19일 : 백엔드 MVP CRUD / 프론트엔드 view 구현  
3월 20일 - 3월 27일 : 백엔드 소켓, 추가기능 구현 / 프론트엔드 디자인 구현  
3월 28일 - 4월 3일 : 배포 / 유저테스트, 버그픽스, 피드백 반영



<br/>


## 🏠 Architecture

---
![image](https://user-images.githubusercontent.com/97423483/161707174-4e2bba24-c745-4448-9f5b-81af162737c9.png)

<br/>
<br/>

## 📃 ERD

---
![스크린샷(167)](https://user-images.githubusercontent.com/97423483/161708729-9230946c-b903-4781-b70a-0a4f628956e2.png)

<br/>
<br/>

## 🔗 API 

---
[API 설계 노션 페이지](https://spiritual-notebook-05f.notion.site/API-f96da817c6eb4474a6988a34778b765d)

<br/>
<br/>


## 💡 Core Function
<details>
<summary>기본 기능</summary>
<img width="423" alt="스크린샷 2022-04-05 오후 7 11 49" src="https://user-images.githubusercontent.com/97423483/161731808-f85f35db-ea47-4530-80bf-7f0a74002d39.png">
<br/>  

- 완결 소설을 읽을 수 있고, 미참여 소설에 참여할 수 있습니다.
- 다양한 기준으로 소설들이 추천되고, 소설에는 좋아요를 누르거나 북마크를 해둘 수 있습니다.
- 소설 첫문단 작성 시에 카테고리를 하나 설정할 수 있고, 마지막 문단을 쓴느 사람이 카테고리를 하나 더 추가할 수 있습니다.
- 카테고리 별로도 소설을 볼 수 있습니다.
</details>
<details>
<summary>작성 중 표시</summary>
<img width="423" alt="스크린샷 2022-04-05 오후 6 57 11" src="https://user-images.githubusercontent.com/97423483/161729304-dceca17f-3a89-4882-a776-f027a08f71f8.png">

- 다른 유저가 문단을 작성하고 있지 않다면 ‘작성 시작하기’ 버튼을 눌러 작성을 시작할 수 있습니다.  
- 작성 중인 유저는 15분의 시간제한을 가지며, 아래에 남은 시간이 표시됩니다.  
<br/>
  <img width="425" alt="스크린샷 2022-04-05 오후 7 04 51" src="https://user-images.githubusercontent.com/97423483/161730640-ba17093f-8edd-46e9-9ccc-3284d257c576.png">
- 작성을 시작한 유저가 아닌 다른 유저들은 Socket통신을 통해 버튼을 비 활성화합니다.
- 문단을 유저가 완성을 시키면 페이지의 모든 유저들에게 Socket통신을 통해 알려주고 리렌더링(Re-Rendering)하여 내용을 실시간으로 보여줍니다.
<br/>
<br/>
- 등록된 문단에 좋아요를 누르고 댓글을 남길 수 있으며 소설에 참여한 참여자 명단을 볼 수 있습니다. 
<br/>
</details>
<details>
<summary>실시간 알림</summary>
<img width="424" alt="스크린샷 2022-04-05 오후 7 16 44" src="https://user-images.githubusercontent.com/97423483/161732567-09d63317-3ec0-4eb1-8c17-8eb1fe3b161d.png">
<br/>  

- 내가 참여한 소설에 다른 사람이 문단을 작성하거나, 소설이나 문단에 좋아요가 등록될 때 등 서비스를 보다 편리하게 이용할 수 있게 다양한 실시간 알림을 보내주고 있습니다.
</details>
<details>
<summary>스탑 앤 고 시스템</summary>
</details>

---





## 🛠  Tech Tools

---

BE :
<div align=center> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/spring data jpa-F28D1A?style=for-the-badge&logo=springdatajpa&logoColor=white">
<img src="https://img.shields.io/badge/websocket-010101?style=for-the-badge&logo=socket.io&logoColor=white">
  <br>
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/aws ec2-07C160?style=for-the-badge&logo=amazonaws&logoColor=white">
  <img src="https://img.shields.io/badge/amazon s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
  <br>

<img src="https://img.shields.io/badge/github actions-2088FF?style=for-the-badge&logo=github actions&logoColor=white">
  <img src="https://img.shields.io/badge/aws codedeploy-9D1620?style=for-the-badge&logo=amazonaws&logoColor=white">
  <img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">
  </div>


<div align=center> 
<img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/googleanalytics-E37400?style=for-the-badge&logo=googleanalytics&logoColor=white">
<img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white"/> 
</div>

<br/>

---

FE:  
<div align=center> 

<img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black">
<img src="https://img.shields.io/badge/redux-764ABC?style=for-the-badge&logo=react&logoColor=black">
<img src="https://img.shields.io/badge/axios-007CE2?style=for-the-badge&logo=axios&logoColor=white">
<img src="https://img.shields.io/badge/reactrouterdom-CA4245?style=for-the-badge&logo=reactrouterdom&logoColor=white">

<img src="https://img.shields.io/badge/styledcomponents-DB7093?style=for-the-badge&logo=styledcomponents&logoColor=white">
<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> 
<img src="https://img.shields.io/badge/route53-F7A81B?style=for-the-badge&logo=route53&logoColor=white">
<img src="https://img.shields.io/badge/cloudfront-04ACE6?style=for-the-badge&logo=cloudfront&logoColor=white">
<br>
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/googleanalytics-E37400?style=for-the-badge&logo=googleanalytics&logoColor=white">
<img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white"/> 

</div>


<br/>
<br/>

## 🎥 데모영상 
Demo link : 

## 🪄 팀노션 
https://spiritual-notebook-05f.notion.site/WeWrite-8f2948c6dc1b472f81e28085f2e189e0

## 📒 팀깃헙
BE https://github.com/binscot/HanghaeFinal  
FE https://github.com/peppermintt0504/Project





