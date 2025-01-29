import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HighestIncrease {


    private static int[] findMaxPair(List<Integer> inputData) {
        int arr[] = new int[3];
        int pairDiff;
        for(int i=0; i<inputData.size(); i++){
            for(int j=i+1; j<inputData.size(); j++){
                if(inputData.get(i)<inputData.get(j)){
                    pairDiff = inputData.get(j)-inputData.get(i);
                    if(pairDiff > arr[0]) {
                        arr[0] = pairDiff;
                        arr[1] = j;
                        arr[2] = i;
                    }
                }
            }
        }
        return arr;
    }


    private static int[] getCrossMinMax(List<Integer> inputData, int l, int m, int r) {
        int[] minMax = new int[3];
        int minIndex = l;
        int maxIndex = m+1;
        for (int i = l+1; i <= m; i++) {
            if(inputData.get(i)<inputData.get(minIndex)) {
                minIndex = i;
            }
        }

        for(int i = m+2; i <= r; i++) {
            if(inputData.get(i) > inputData.get(maxIndex)) {
                maxIndex = i;
            }
        }
        minMax[0] = minIndex;
        minMax[1] = maxIndex;
        minMax[2] = inputData.get(maxIndex) - inputData.get(minIndex);
        return minMax;
    }

    private static int[] binarySearch(List<Integer> inputData, int l, int r) {
        int[] res;
        if(l==r) {
            res = new int[3];
            res[0] = l;
            res[1] = l;
            res[2] = Integer.MIN_VALUE;
            return res;
        }
        int mid = l + (r-l)/2;
        int[] leftMinMax = binarySearch(inputData, l, mid);
        int[] rightMinMax = binarySearch(inputData, mid+1, r);
        int[] crossMinMax = getCrossMinMax(inputData, l, mid, r);
        res = leftMinMax[2]>rightMinMax[2]? leftMinMax : rightMinMax;
        res = res[2]>crossMinMax[2]? res : crossMinMax;
        return res;
    }

    private static int[] findMaxPairOptimized(List<Integer> inputData) {
        return binarySearch(inputData, 0, inputData.size()-1);
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Integer> inputs = new ArrayList<>();
        while (scanner.hasNextInt()) {
            inputs.add(scanner.nextInt());
            if(scanner.hasNext("stop")) {
                break;
            }
        }
        int[] arr = findMaxPairOptimized(inputs);
        System.out.println("Highest increase: "+inputs.get(arr[1])+" - "+"("+inputs.get(arr[0])+") = "+arr[2]);
    }
}
