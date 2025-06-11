class Solution {
    public int[] sortArray(int[] nums) {
        quick(nums, 0, nums.length - 1);
        return nums;
    }

    static void quick(int[] nums, int low, int high) {
        // Base condition: single element or empty subarray
        if (low >= high) return;

        int s = low;
        int e = high;
        int mid = (s + e) / 2;
        int pivot = nums[mid]; // Pivot chosen as middle element

        // Partition the array around the pivot
        while (s <= e) {
            while (nums[s] < pivot) {
                s++;
            }
            while (nums[e] > pivot) {
                e--;
            }

            if (s <= e) {
                // Swap elements on the wrong side
                int temp = nums[s];
                nums[s] = nums[e];
                nums[e] = temp;
                s++;
                e--;
            }
        }

        // Recursively sort the two halves
        quick(nums, low, e);
        quick(nums, s, high);
    }
}
