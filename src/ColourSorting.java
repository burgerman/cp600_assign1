import java.util.Random;

public class ColourSorting {

    private static int RED = 0;
    private static int WHITE = 1;
    private static int BLUE = 2;

    private static void swap(int arr[], int l, int r) {
        arr[l] = arr[l]^arr[r];
        arr[r] = arr[l]^arr[r];
        arr[l] = arr[l]^arr[r];
    }

    private static void colorSort(int arr[]) {
        int left = 0, mid =0, right = arr.length - 1;
        while(mid <= right) {
            if (arr[mid] < WHITE) {
                swap(arr, left, mid);
                mid++;
                left++;
            } else if (arr[mid] > WHITE) {
                swap(arr, mid, right);
                right--;
            } else {
                mid++;
            }
        }
        // check the boundary of left-mid and right-mid
        if(arr[mid] < arr[left]) {
            swap(arr, left, mid);
        }
    }


    public static void main(String[] args) {
        Random random = new Random();
        int n = 20;
        int [] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = random.nextInt(3);
        }
        System.out.println("Colors:");
        for (int i = 0; i < n; i++) {
            if(arr[i]==RED) {
                System.out.print(" RED");
            } else if(arr[i]==WHITE) {
                System.out.print(" WHITE");
            } else if(arr[i]==BLUE) {
                System.out.print(" BLUE");
            }
        }
        colorSort(arr);
        System.out.println();
        System.out.println("Sorted Colors:");
        for (int i = 0; i < n; i++) {
            if(arr[i]==RED) {
                System.out.print(" RED");
            } else if(arr[i]==WHITE) {
                System.out.print(" WHITE");
            } else if(arr[i]==BLUE) {
                System.out.print(" BLUE");
            }
        }

    }

}
