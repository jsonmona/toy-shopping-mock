## 안드로이드 쇼핑앱 목업

### 개요
모바일프로그래밍 과목의 중간과제로 간단한 쇼핑 앱 목업을 제작했습니다.
모든 기능은 오프라인상에서 작동하며 인터넷 연결을 요구하지 않습니다.

API 32 (Android 12L)을 타겟하고, 최소 API 21 (Android Lollipop) 이상의 기기에서 동작합니다.

디버그용으로 빌드된 APK 파일이 릴리즈 섹션에 있습니다.

### 기능
 * 로그인 및 회원가입 페이지
 * 상품 리스트 보는 페이지
 * 회원정보 페이지
 * SQLite3 내장 DB를 이용함
 * 상품 추가 가능
 * 비로그인시 계정 정보를 누르면 회원가입 페이지로 가겠냐고 물어봄
 * 회원가입시 아이디 중복체크
 * 전화번호 입력시 자동으로 하이픈 추가
 * 비밀번호 8자리 이상이고 특수기호가 들어있는지 체크 (알파벳과 숫자가 포함되었는지는 체크하지 않음)
 * 회원정보 페이지에서 정보 수정시 자동으로 DB반영
 * 로그인된 상태에서 뒤로가기 버튼을 누르면 앱이 바로 꺼지지만, 로그인하지 않은 상태에서는 로그인 페이지로 돌아감

### 참고사항
회원가입 하겠냐고 물어보는 다이얼로그는 에뮬레이터에서 보면 CANCEL 과 OK가 뜹니다.
영어가 뜨는 이유는 시스템 언어가 영어로 설정되어있기 때문이며, 시스템 언어를 바꾸면 한국어로 뜹니다.

비밀번호는 SHA-256 해시로 저장합니다.
솔트를 사용하지 않기 때문에 보안상 취약하므로 진짜 비밀번호를 저장하지는 마십시오.
