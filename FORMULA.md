1\. 현재 레벨에서 다음 레벨까지 필요한 경험치 포인트 구하는 함수
---
```java
com.noeul.discord.hk.leaderboard.getExpUpTo(long level) // long
```
에 쓰인 공식은 다음과 같다. 이 때, level 값은 x로 들어간다.

<img src="https://github.com/NoEul1234/md-test/raw/master/img/formula_upto_nextlevel.png" width="50%">

2\. 누적 경험치 구하는 함수
---
```java
com.noeul.discord.hk.leaderboard.getTotalExp(long level, long currentExp) // long
```
에 쓰인 공식은 다음과 같다. 이 때, level 값은 x로 들어간다.

<img src="https://github.com/NoEul1234/md-test/raw/master/img/formula_totalexp.png" width="50%">

최종 값은 이 공식에 있는 함수를 취한 후 currentExp 값을 더해주면 된다.

3\. 누적 경험치로부터 몇 레벨인지 구하는 함수
---
```java
com.noeul.discord.hk.leaderboard.getLevel(long totalExp) // long
```
에 쓰인 공식은 다음과 같다. 이 때, totalExp 값은 x로 들어간다.

<img src="https://github.com/NoEul1234/md-test/raw/master/img/formula_inversefunc_totalexp.png" width="50%">

2번 문단에서 언급한 공식의 역함수이다.
최종 값은 이 공식에 있는 함수를 취한 후 바닥 함수 floor(x) 를 취하면 된다.
