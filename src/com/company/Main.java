package com.company;


import javax.xml.soap.Node;
import java.util.*;


/**
 * TODO 제출전: 맞춤법, 주석, 변수,메소드 명 체크
 * TODO 예외 처리(puzzleSize == 0, puzzleSize == 1, puzzle 입력값이 size 넘는 경우, 음수가 들어오는 경우, puzzleSize = 3이상이 들어오는 경우 A* 알고리즘 alert)
 * TODO 시간 계산 넣기
 *
 * solvalbleCheck 역전 카운트란 초기 상태와 목표 상태에서 이동 가능유무를 파악하는 조건 -> 적용 안될 때 return -1과 print 메시지, 초기화를 해주자.
 *
 */
public class Main {



    //입력 값으로 퍼즐 생성
    public static int createPuzzleItem(Scanner sc) {
        System.out.print("생성할 puzzle의 사이즈를 입력하세요:");
        int puzzleSize = sc.nextInt();
        int[][] puzzleArr = new int[puzzleSize][puzzleSize];
        System.out.println("-----------------------------");
        System.out.println("완성해야할 퍼즐 모양 = " + Arrays.deepToString(createFinalPuzzleForm(puzzleSize)));
        System.out.println("-----------------------------");
        System.out.println();

        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                System.out.print("(" + i + "," + j + ") 위치에 생성할 puzzle의 값을 입력하세요:");
                puzzleArr[i][j] = sc.nextInt();
                System.out.println(Arrays.deepToString(puzzleArr));
            }
        }
        sc.close();
        System.out.println();

        return isSolvablePuzzleCheck(puzzleSize, puzzleArr);
    }

    public static int[][] createFinalPuzzleForm(int puzzleSize) {
        int[][] defaultPuzzleArr = new int[puzzleSize][puzzleSize];
        int startValue = 1;

        for (int i = 0; i < puzzleSize; i++) {
            for(int j = 0; j < puzzleSize; j++) {
                if (i == puzzleSize-1 && j == puzzleSize-1) {
                    defaultPuzzleArr[i][j] = 0;
                    break;
                }
                defaultPuzzleArr[i][j] = startValue;
                startValue++;
            }
        }
        return defaultPuzzleArr;
    }

    public static int[] transformNonLinearToLinear(int puzzleLength, int[][] puzzleArr) {
        int[] linearPuzzleArr = new int[puzzleLength];
        int k = 0;

        for (int i = 0; i < puzzleArr.length; i++) {
            for (int j = 0; j < puzzleArr.length; j++) {
                linearPuzzleArr[k] = puzzleArr[i][j];
                k++;
            }
        }
        return linearPuzzleArr;
    }

    // 생성된 퍼즐 전달 및 해당 퍼즐로 문제 해결 가능 유무 체크
    // 역전 카운트 부분 병합정렬 로직 - 근데 이걸 왜 써야할까? -> 0을 제외한 앞뒤 숫자를 비교해서 역전 카운트 숫자를 구해서 정렬 할 수 있는지 없는지 체크한다.
    // 역전 카운트 숫자를 통해서 어떻게 정렬할 수 있는지 없는지 구분할 수 있을까?
    // 주어진 배열에서 자신의 앞에 있는 수중 자신보다 크기가 큰 수의 갯수를 찾기 위한 방법
    public static int isSolvablePuzzleCheck(int puzzleSize, int[][] puzzleArr) {
        int puzzleLength = puzzleSize * puzzleSize;
        int[] linearPuzzleArr = transformNonLinearToLinear(puzzleLength, puzzleArr);
        int inversionCount = calculateInversionCount(puzzleLength, linearPuzzleArr);
        System.out.println("-----------------------------");
        System.out.println("linearPuzzleArr: " + Arrays.toString(linearPuzzleArr));
        System.out.println("inversionCount: " + inversionCount);
        System.out.println("-----------------------------");

        //puzzleSize가 홀수 이면서, inversionCount가 짝수일 때
        if (puzzleSize % 2 != 0 && inversionCount % 2 == 0) {
            System.out.println("puzzleSize가 홀수의 경우이면서 inversionCount가 짝수일 때 로직 실행");
            return findMovingCount(puzzleArr);
        }

        //puzzleSize가 짝수 일때
        if (puzzleSize % 2 == 0) {
            //빈칸의 행의 위치가 홀수인지 짝인지  찾기
            boolean isEvenEmptyRow = isEvenEmptyRow(puzzleArr);

            System.out.println("뒤에서 부터 계산한 evenEmptyRow의 짝수='true', 홀수='false': " + isEvenEmptyRow);
            //빈값(0)의 위치가 짝수이면서, 역전 카운트가 홀수일 때 이동 가능
            if (isEvenEmptyRow && inversionCount % 2 != 0) {
                System.out.println("빈값(0)의 위치가 짝수이면서, 역전 카운트가 홀수일 때 실행");
                return findMovingCount(puzzleArr);
            }

            //빈값(0)의 위치가 홀수이면서, 역전 카운트가 짝수일때 이동가능
            if (!isEvenEmptyRow && inversionCount % 2 == 0) {
                System.out.println("빈값(0)의 위치가 홀수이면서, 역전 카운트가 짝수일때 실행" );
                return findMovingCount(puzzleArr);
            }
            System.out.println("-----------------------------");
            System.out.println("해당 퍼즐은 풀 수 없는 퍼즐입니다.");
            return -1;
        }
        System.out.println("-----------------------------");
        System.out.println("해당 퍼즐은 풀 수 없는 퍼즐입니다.");
        return -1;
    }

    public static boolean isEvenEmptyRow(int[][] puzzleArr) {
        int emptyRowIndex = -1;

        for (int i = puzzleArr.length -1; i >= 0; i--) {
            for (int j = 0; j < puzzleArr.length; j++) {
                if (puzzleArr[i][j] == 0) {
                    emptyRowIndex = i;
                    break;
                }
            }
        }

        if ((puzzleArr.length - emptyRowIndex) % 2 == 0) {
            return true;
        }
        return false;
    }

    public static int calculateInversionCount(int puzzleLength, int[] linearPuzzleArr) {
        int inversionCount = 0;

        for (int i = 0; i < puzzleLength; i++) {
            for (int j = i + 1; j < puzzleLength; j++) {
                if (linearPuzzleArr[i] > 0 && linearPuzzleArr[j] > 0 && linearPuzzleArr[i] > linearPuzzleArr[j]) {
                    inversionCount++;
                }
            }
        }

        return inversionCount;
    }

    // 문제 해결 가능시 최종 이동 횟수 구하는 로직
    // 주입 받은 배열과, 상태가 되어야하는 로직을 가지고
    //  Select Sort로 사용시 경우의 수가 1개일 경우 2개로 카운트 친다 이유가 뭘까?
    //  문제를 BFS로 접근? merge sort로 접근? select Sort로 접근?? 차이는? clear
    public static int findMovingCount(int[][] puzzleArr) {
        int resultCount = 0;
        // puzzleArr에서  노드 좌표 선택하기
        // 노드 좌표 open[]에 넣기
        //


        return resultCount;
}


    public static void main(String[]args) {
        Scanner sc = new Scanner(System.in);
        int puzzle = createPuzzleItem(sc);
        if (puzzle == -1) {
            System.out.println("해당 퍼즐은 문제를 풀 수 없습니다. 입력값을 확인하세요.");
            return;
        }
        System.out.println("총 이동횟수: " + puzzle + "번");
    }
}
