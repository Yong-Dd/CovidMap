<p align="center">
  <img src="https://github.com/Yong-Dd/CovidMap/assets/71385485/3452573e-0534-4576-9dd6-d4cb95945259"/>
</p>
<br/>

# 코로나19 예방접종센터 지도 서비스 
> 공공기관 API를 통해 코로나19 예방접종센터 리스트를 불러와 타입에 따라 지도에 마커로 보여주는 앱입니다.
> 
> 작업기간 : 2023.08.04 ~ 2023.08.07

<br/>
<div>
  <img src="https://img.shields.io/badge/kotlin-6941c2"/>
  <img src="https://img.shields.io/badge/Hilt-2096f2"/>
  <img src="https://img.shields.io/badge/Retrofit2-47b983"/>
  <img src="https://img.shields.io/badge/Coroutine Flow-6941c2"/>
  <img src="https://img.shields.io/badge/Room-2da1b0"/>
  <img src="https://img.shields.io/badge/MVVM-0E5BD8"/>
  <img src="https://img.shields.io/badge/DataBinding-39d787"/>

</div>
<br/>

## 기능

<figure class ="half">
    <img src="https://github.com/Yong-Dd/CovidMap/assets/71385485/dd531b1e-bc49-4127-a87d-f79fbc8ef149"/>
    <img src="https://github.com/Yong-Dd/CovidMap/assets/71385485/f14cc18b-54ea-4d3a-8236-ca35a0287d04"/>
 </figure>

### 1. Splash
- 2초동안 로딩바가 100%가 되도록 구현
  - DB에 저장이 완료되지 않은 경우 저장을 기다렸다가 400ms 걸쳐 100%로 변경 
- API 총 10페이지(perPage 10) 호출하여 DB에 저장 (Room) - CenterDao, CenterDatabase
  

### 2. Map(지도 화면)
- 네이버 지도 사용
- DB에 저장된 값을 불러오고, centerType에 따라 빨간색, 파란색으로 마커 추가
  - 중앙/권역, 지역 외 다른 값이 들어올 것을 대비해 '기타' 추가
- 마커 클릭 시 정보 창이 상단에 뜨고(visible), 같은 마커를 클릭시 없어짐(gone)
  
  - 마커가 떠있는 경우 뒤로가기 버튼 클릭 시 없어짐(gone)
- 현재 위치 버튼 클릭
  - 위치 permission 요청 후 현재 위치로 이동  

<br/>
<br/>


## 주요 API & library
* 공공데이터활용지원센터_코로나19 예방접종센터 조회서비스 <br/>https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15077586 
* google 위치 서비스<br/>com.google.android.gms:play-services-location:21.0.1
* naver 지도<br/>com.naver.maps:map-sdk:3.17.0
