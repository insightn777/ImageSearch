# ImageSearch

사용자가 검색어를 입력하고 이미지 검색 결과를 화면에 표시하는 어플리케이션

검색어 필드에서 1초 이상 추가 입력이 없을 경우 검색 작업을 수행합니다. ( 핸들러를 이용 )

사용 라이브러리: Okhttp3 / Retrofit2 / Gson / Glide4 / architecture components ( paging )

* 이미지 검색 KAKAO REST API 사용  
https://developers.kakao.com/docs/restapi/search#%EC%9D%B4%EB%AF%B8%EC%A7%80-%EA%B2%80%EC%83%89

* 검색 결과  
        - retrofit 으로 받아온 response는 gson으로 바로 data class ImageSearchResponse 의 instance 로 변환되어짐  
        - 포함된 img url ( Livedata ) 은 paging 을 이용해 data source 에서 repository , view model로 전달됨  
        - view model 이 recycler view 어댑터에서 각 아이템들에 img url 을 전달하면 glide 가 이미지를 받아서 화면에 띄움  
        - 이미지의 가로는 화면 폭과 동일, 세로는 원본 비율을 유지하도록 표시  
        - 아래로 스크롤하면 paging 이 추가 이미지 검색결과를 표시하기 위해 다음 페이지에대한 위의 작업을 반복 수행함  

* 에러 처리  
 인터넷 연결이 안됬을 시 main 화면 text view 에서 알려주며, Loading 중일 때는 progress indicator 가 recycler view 에 노출되고,  검색 결과가 없거나 네트워크 오류, 이미지 다운 오류가 발생했을 경우, 사용자에게 알려주도록 함
