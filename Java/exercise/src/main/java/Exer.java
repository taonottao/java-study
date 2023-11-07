import java.util.Arrays;

/**
 * 从{1,2,3,4,5,6,7,8,9}中随机挑选不重复的5个数字作为输入数组'selectedDigits'，
 * 能组成多少个互不相同且无重复数字的3位数？请编写程序，从小到大顺序，以数组形式，输这些3位数。
 */
public class Exer {

    //1 2 3 4 5
    public static int[] ThreeDigitNumbers (int[] selectedDigits) {
        Arrays.sort(selectedDigits);
        int a = selectedDigits[0];
        int b = selectedDigits[1];
        int c = selectedDigits[2];
        int d = selectedDigits[3];
        int e = selectedDigits[4];
        int[] ret = new int[60];
        int i = 0;
        int[] bai = {a*100, b*100, c*100, d*100, e*100};
        int[] shi = {a*10, b*10, c*10, d*10, e*10};
        int[] ge = {a, b, c, d, e};

        for (int j = 0; j < 5; j++) {
            for (int k = 0; k < 5; k++) {
                if(k == j){
                    continue;
                }
                for (int l = 0; l < 5; l++) {
                    if (l == j || l == k) {
                        continue;
                    }
                    ret[i] += bai[j] + shi[k] + ge[l];
                    i++;
                }
            }
        }

        return ret;
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        int[] ints = ThreeDigitNumbers(arr);
        for (int i = 0; i < ints.length; i++) {
            System.out.print(ints[i] + " ");
        }
        System.out.println();
    }

    public int[] calculateFinalPositi(String instructions) {
        char[] ch = instructions.toCharArray();
        int m = 0;// 上下
        int n = 0;// 左右
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == 'U') {
                m++;
            } else if (ch[i] == 'D') {
                m--;
            } else if (ch[i] == 'R') {
                n++;
            } else if (ch[i] == 'L') {
                n--;
            }
        }
        int[] res = new int[2];
        res[0] = n;
        res[1] = m;
        return res;
    }
}
