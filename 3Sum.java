import java.util.*;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        // Step 1: Sort the array
        Arrays.sort(nums);

        // Step 2: Loop through the array
        for (int i = 0; i < nums.length - 2; i++) {

            // Skip duplicates for the first number
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int left = i + 1;
            int right = nums.length - 1;

            // Step 3: Two-pointer approach
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;

                    // Skip duplicates for the second number
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }

                } else if (sum < 0) {
                    left++; 
                } else {
                    right--; 
                }
            }
        }

        return result;
    }
}
