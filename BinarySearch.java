import java.util.Arrays;
import java.util.Scanner;

public class BinarySearch {
    public static int binarySearch(int[] arr, int target) {
        Arrays.sort(arr); // Sort the array
        int low = 0;
        int h = arr.length - 1;
        while (low <= h) {
            int m = low + (h - low) / 2;
            if (arr[m] == target) {
                return m;
            } else if (arr[m] < target) {
                low = m + 1;
            } else {
                h = m - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the size:");
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.println("Enter the elements:");
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }
        System.out.println("Enter the target:");
        int target = sc.nextInt();
        int result = binarySearch(arr, target);
        if (result == -1) {
            System.out.println("Element not found");
        } else {
            System.out.println("Element is found at index: " + result);
        }
    }
}
