import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HybridSort {

    private static void doWrite(String path, List<Integer> source) {
        if(source!=null && source.size()>0 && path!=null && path.length()>0) {
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
                for(Integer i : source) {
                    bw.write(Integer.toString(i));
                    bw.newLine();
                }
                bw.flush();
            } catch (IOException e) {
            }
        }
    }

    private static void generateRandomInteger(String path) {
        Random random = new Random();
        int dataSize = 200000;
        int randomInt;
        List<Integer> source = new ArrayList<>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            randomInt = random.nextInt(dataSize);
            source.add(randomInt);
        }
        doWrite(path, source);
    }


    private static void swap(int arr[], int l, int r) {
        int tmp = arr[l];
        arr[l] = arr[r];
        arr[r] = tmp;
    }

    private static void BubbleSort(int[] arr) {
        int n = arr.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    // Swap arr[j] and arr[j+1]
                    swapped = true;
                }
            }
            // Early stopping if arrary was already sorted
            if (swapped == false)
                break;
        }
    }

    private static void insertionSort(int[] arr, int start, int end) {
        for (int i = start+1; i<end; i++) {
            int toBeInserted = arr[i];
            int j = i;
            // Move those elements greater than the to-be-inserted to its right
            while(j>start && toBeInserted < arr[j-1]) {
                arr[j] = arr[j-1];
                j--;
            }
            // insert element into the current pos of j
            if(i!=j) {
                arr[j] = toBeInserted;
            }
        }
    }

    private static void merge(int[] arr, int l, int m, int r) {
        int i,j,k;
        int size1 = m-l+1;
        int size2 = r-m;
        int[] arr1 = new int[size1];
        int[] arr2 = new int[size2];
        for (i = 0; i < size1; i++) {
            arr1[i] = arr[l+i];
        }
        for (j = 0; j < size2; j++) {
            arr2[j] = arr[m+1+j];
        }
        i=0; j=0; k=l;
        while(i<size1 && j<size2) {
            if(arr1[i]<=arr2[j]) {
                arr[k] = arr1[i];
                i++;
            } else {
                arr[k] = arr2[j];
                j++;
            }
            k++;
        }
        while(i<size1) {
            arr[k] = arr1[i];
            k++;
            i++;
        }
        while(j<size2) {
            arr[k]=arr2[j];
            k++;
            j++;
        }
    }

    private static void mergeSort(int[] arr, int l, int r) {
        if(l<r) {
            int m = l+(r-l)/2;
            mergeSort(arr, l, m);
            mergeSort(arr, m+1, r);
            merge(arr, l, m, r);
        }
    }



    private static int partitionSelect(int[] arr, int l, int r, int m) {
        int pivot = arr[m];
        swap(arr, m, r);
        int dest = l;
        for(int i=l; i<r; i++) {
            if(arr[i]<pivot) {
                if(i!=dest) {
                    swap(arr, dest, i);
                }
                dest++;
            }
        }
        swap(arr, dest, r);
        return dest;
    }

    /**
     * Leverage median-of-medians algorithm to find a suited pivot
     * For this algorithm, it groups array into multiple arrays,
     * 5 elements as a sub-array, then find out its median
     * All medians of sub-arrays as a new array of medians
     * Leverage insertion sort to sort each 5-element sub-array
     * Lastly, return the median of the array of medians as the pivot of the original array
     * The time complexity of this algorithm is O(n)
     */
    private static int select(int[] arr, int left, int right) {

        if(left==right) {
            return left;
        }

        // group elements into small groups of 5
        if(right-left+1<=5) {
            insertionSort(arr, left, right+1);
            // return median of the subgroup
            return left+ (right - left) / 2;
        }

        int groupNum;
        if((right - left + 1)%5==0) {
            groupNum = (right - left + 1)/5;
        } else{
            groupNum = (right - left + 1)/5+1;
        }

        int[] medians = new int[groupNum];
        int medianIndex = 0;
        int r, sub_median;
        for(int i=left; i<=right; i+=5) {
            r = (i+4)>right?(right):(i+4);
//            medians[medianIndex] = getMedian(arr, i, r);

            insertionSort(arr, i, r+1);
            sub_median = i+ (r-i) / 2;
            medians[medianIndex] = arr[sub_median];
            medianIndex++;
        }
        insertionSort(medians, 0, medians.length);
        int median = medians.length/2;
        for(int i=left; i<=right; i++) {
            if(arr[i]>=medians[median]-5 && arr[i]<=medians[median]+5) {
                median = i;
                break;
            }
        }
        return partitionSelect(arr, left, right, median);
    }


    private static int partition(int[] arr, int low, int high) {
        // Choosing the pivot element
        int[] subArr = Arrays.copyOfRange(arr, low, high + 1);
        int pivot = select(subArr, 0, subArr.length-1);
        for(int k = low; k<=high; k++) {
            if(arr[k] >= subArr[pivot]-5 && arr[k]<=subArr[pivot]+5) {
                pivot = k;
                break;
            }
        }
        swap(arr, pivot, high);
        pivot = arr[high];
        int i = (low - 1); // Index of smaller element

        for (int j = low; j < high; j++) {
            // If current element is smaller than or equal to pivot
            if (arr[j] <= pivot) {
                i++;
                // Swap arr[i] and arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Swap arr[i+1] and arr[high] (or pivot)
        swap(arr, i + 1, high);
        return i + 1;
    }



    private static void quicksort(int[] arr, int left, int right) {
        if(left<right) {
            // Partitioning index
            int pi = partition(arr, left, right);

            // Recursively sort elements before and after partition
            quicksort(arr, left, pi - 1);
            quicksort(arr, pi + 1, right);
        }
    }

    /**
     * The idea of this hybrid sort is to combine merge sort with quicksort based on median-of-medians algorithm.
     * The idea is inspired by another solution "TimSort", a built-in hybrid approach in Python or Java.
     * For all sub-array whose size larger than 286, leverage merge sort to divide the problem into sub-problems.
     * For sub-problems, we make use of quicksort to mitigate the overheads of recursion in merge sort.
     * To achieve time complexity O(NlogN), the core problem of quicksort is choosing a suited pivot when partitioning.
     * In the traditional quicksort, it simply uses the right end of the array as the pivot,
     * which may lead to skewed partition, and time complexity will be O(N^2).
     * The ideal pivot should partition the problem half-half. However, it's difficult to find the ideal efficiently.
     * As a trade-off, we use median-of-medians algo to find a good pivot in an approximate approach,
     * whose time complexity is O(N).
     *
     * The overall time complexity of this hybrid solution remains O(NlogN), because the dominated term is merge sort.
     * Though quicksort based on median-of-medians algorithm is O(NlogN), its problem size is fixed, which less than 286.
     * 286 is a reference size given by TimSort in Java. The extra time of merge is still O(N).
     */
    private static void hybridSort(int[] arr, int l, int r) {
        if((r-l)<286) {
            quicksort(arr, l, r);
        } else {
            if(l<r) {
                int m = l+(r-l)/2;
                hybridSort(arr, l, m);
                hybridSort(arr, m+1, r);
                merge(arr, l, m, r);
            }
        }
    }


    public static void main(String[] args) {
//        String path = "input_data.txt";
//        generateRandomInteger(path);
        String classPath = HybridSort.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String filePath = classPath+"/input_data.txt";
        File dictFile = new File(filePath);
        if(dictFile.exists()) {
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(dictFile))) {
                int lineNum = (int) Files.lines(Paths.get(dictFile.getAbsolutePath())).count();
                int [] arr = new int[lineNum];
                int [] arr2 = new int[lineNum];
                int [] arr3 = new int[lineNum];
                int index=0;
                String word;
                int tmpInt;
                while((word=bufferedReader.readLine())!=null) {
                    tmpInt = Integer.parseInt(word);
                    arr[index] = tmpInt;
                    arr2[index] = tmpInt;
                    arr3[index] = tmpInt;
                    index++;
                }
                long startTime, endTime;
                startTime = System.nanoTime();
                BubbleSort(arr);
                endTime = System.nanoTime();
                // Bubble Sort Time Cost: 12 seconds for 100,000 problem size
                System.out.println("Bubble Sort Time Cost: " +TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS) + " seconds");
                String outputFile1 = "bubble_sort.txt";
                doWrite(outputFile1, Arrays.stream(arr).boxed().collect(Collectors.toList()));
                int left=0, right=arr2.length-1;
                startTime = System.nanoTime();
                mergeSort(arr2, left, right);
                endTime = System.nanoTime();
                // Merge Sort Time Cost: 11 milliseconds for 100,000 problem size
                System.out.println("Merge Sort Time Cost: " + TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS)+ " milliseconds");
                String outputFile2 = "merge_sort.txt";
                doWrite(outputFile2, Arrays.stream(arr2).boxed().collect(Collectors.toList()));
//                System.out.println("median: " + select(arr3, 0, arr.length-1));
                startTime = System.nanoTime();
                hybridSort(arr3, 0, arr3.length-1);
                endTime = System.nanoTime();
                // Hybrid Sort Time Cost: 17 milliseconds for 100,000 problem size
                System.out.println("Hybrid Sort Time Cost: " + TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS)+ " milliseconds");
                String outputFile3 = "hybrid_sort.txt";
                doWrite(outputFile3, Arrays.stream(arr3).boxed().collect(Collectors.toList()));

            } catch (IOException io) {
            }
        }
    }
}