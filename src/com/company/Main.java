package com.company;


import java.util.*;


/**
 * TODO 제출전: 맞춤법, 주석, 변수,메소드 명 체크
 * TODO 예외 처리(puzzleSize == 0, puzzleSize == 1, puzzle 입력값이 size 넘는 경우, 음수가 들어오는 경우, puzzleSize = 3이상이 들어오는 경우 A* 알고리즘 alert)
 * TODO 시간 계산 넣기
 * TODO static -> 변경 clear
 * TODO 3입력시 랜덤 3 *3 퍼즐 생성하는걸로? 아님 입력?
 * solvalbleCheck 역전 카운트란 초기 상태와 목표 상태에서 이동 가능유무를 파악하는 조건 -> 적용 안될 때 return -1과 print 메시지, 초기화를 해주자.
 */
public class Main {

    private int createPuzzleItem(Scanner sc) throws IllegalArgumentException {
        int puzzleSize = sc.nextInt();

        if (puzzleSize == 0 || puzzleSize == 1) {
            throw new IllegalArgumentException("0 또는 1을 입력할 수 없습니다.");
        }

        int[][] puzzleArr = new int[puzzleSize][puzzleSize];

        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                System.out.print("(" + i + "," + j + ") 위치에 생성할 puzzle의 값을 입력하세요:");
                int value = sc.nextInt();
                if (value < 0 || value >= puzzleSize * puzzleSize) {
                    throw new IllegalArgumentException("잘못된 퍼즐 사이즈를 입력하였습니다.");
                }
                puzzleArr[i][j] = value;
                System.out.println(Arrays.deepToString(puzzleArr));
            }
        }

        System.out.println();
        return isSolvablePuzzleCheck(puzzleSize, puzzleArr);
    }

    private int[] transformNonLinearToLinear(int puzzleLength, int[][] puzzleArr) {
        int[] linearPuzzleArr = new int[puzzleLength];
        int index = 0;

        for (int i = 0; i < puzzleArr.length; i++) {
            for (int j = 0; j < puzzleArr.length; j++) {
                linearPuzzleArr[index] = puzzleArr[i][j];
                index++;
            }
        }
        return linearPuzzleArr;
    }

    // 얼리 리턴 적용해보기
    private int isSolvablePuzzleCheck(int puzzleSize, int[][] puzzleArr) {
        int puzzleLength = puzzleSize * puzzleSize;
        int[] linearPuzzleArr = transformNonLinearToLinear(puzzleLength, puzzleArr);
        int inversionCount = calculateInversionCount(puzzleLength, linearPuzzleArr);

        if (puzzleSize % 2 != 0 && inversionCount % 2 == 0) {
            return goMovingCount(puzzleSize, puzzleArr, 0);
        }

        if (puzzleSize % 2 == 0) {
            boolean isEvenEmptyRow = isEvenEmptyRow(puzzleArr);

            if (isEvenEmptyRow && inversionCount % 2 != 0) {
                return goMovingCount(puzzleSize, puzzleArr, 0);
            }

            if (!isEvenEmptyRow && inversionCount % 2 == 0) {
                return goMovingCount(puzzleSize, puzzleArr, 0);
            }
            return -1;
        }
        return -1;
    }

    private boolean isEvenEmptyRow(int[][] puzzleArr) {
        int emptyRowIndex = -1;

        for (int i = puzzleArr.length - 1; i >= 0; i--) {
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

    //TODO insert sort -> merge sort로 변경하기
    private int calculateInversionCount(int puzzleLength, int[] linearPuzzleArr) {
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

    private boolean isFinish(int puzzleSize, int[][] puzzleArr) {
        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                int defaultValue = (i * puzzleSize + j) + 1;
                int compareValue = puzzleArr[i][j];

                if (i == puzzleSize - 1 && j == puzzleSize - 1) {
                    defaultValue = 0;
                }

                if (defaultValue != compareValue) {
                    return false;
                }
            }
        }
        return true;
    }

    private Pos findZeroPosition(int puzzleSize, int[][] puzzleArr) {
        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                if (puzzleArr[i][j] == 0) {
                    return new Pos(i, j);
                }
            }
        }
        return null;
    }

    private int goMovingCount(int puzzleSize, int[][] puzzleArr, int movingCount) {
        if (isFinish(puzzleSize, puzzleArr)) {
            return movingCount;
        }

        Pos zeroPosition = findZeroPosition(puzzleSize, puzzleArr);

        //TODO 0이 가려고하는 위치
        int minimumValue = puzzleSize * puzzleSize;
        int nextZeroDir = 0;
        //상우하좌 탐색 하며, 탐색 상태의 0의 위치를 만드는 배열 newArr를 통해 복사전 배열 생성
        for (int p = 0; p < 4; p++) {
            int newRow = zeroPosition.moveRow(p);
            int newCol = zeroPosition.moveCol(p);
            int[][] newArr = new int[puzzleSize][puzzleSize];

            if (newRow < 0 || newCol < 0 || newRow > puzzleSize - 1 || newCol > puzzleSize - 1) {
                continue;
            }

            for (int i = 0; i < puzzleSize; i++) {
                newArr[i] = puzzleArr[i].clone();
            }

            //
            int temp = newArr[newRow][newCol];
            newArr[newRow][newCol] = newArr[zeroPosition.getRow()][zeroPosition.getCol()];
            newArr[zeroPosition.getRow()][zeroPosition.getCol()] = temp;

            int diff = 0;
            for (int i = 0; i < puzzleSize; i++) {
                for (int j = 0; j < puzzleSize; j++) {
                    System.out.printf("%d", newArr[i][j]);
                    if (newArr[i][j] == 0) {
                        continue;
                    }

                    int defaultValue = (i * puzzleSize + j) + 1;

                    if (defaultValue != newArr[i][j]) {
                        diff++;
                    }
                }
                System.out.println();
            }
            int HeuristicsValue = (movingCount + 1) + diff;

            if (minimumValue > HeuristicsValue) {
                minimumValue = HeuristicsValue;
            }
        }

        int[][] newArr = new int[puzzleSize][puzzleSize];
        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                newArr[j] = puzzleArr[j].clone();
            }
        }
        Pos newPosition = new Pos(zeroPosition.moveRow(nextZeroDir), zeroPosition.moveCol(nextZeroDir));

        int temp = newArr[newPosition.getRow()][newPosition.getCol()];
        newArr[newPosition.getRow()][newPosition.getCol()] = newArr[zeroPosition.getRow()][zeroPosition.getCol()];
        newArr[zeroPosition.getRow()][zeroPosition.getCol()] = temp;

        return goMovingCount(puzzleSize, newArr, movingCount + 1);
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Main main = new Main();

        try {
            int puzzle = main.createPuzzleItem(sc);
            System.out.println("총 이동횟수: " + puzzle + "번");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("알 수 없는 에러가 발생했습니다. 재실행 해주시기 바랍니다.");
        } finally {
            sc.close();
        }
    }
}

class Pos {
    int row;
    int col;

    final int[] dRow = {-1, 0, 1, 0};
    final int[] dCol = {0, 1, 0, -1};

    public Pos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int moveRow(int dir) {
        return this.row + dRow[dir];
    }

    public int moveCol(int dir) {
        return this.col + dCol[dir];
    }
}