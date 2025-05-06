class Solution {
    public int[] twoSum(int[] nums, int target) {
        int n = nums.length;
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[0];
    }
public static void main(String[] args) {
        Solution solution = new Solution();
        int score = 0;
        int totalTests = 5;
        
        // 测试用例1: 普通情况
        int[] nums1 = {2, 7, 11, 15};
        int target1 = 9;
        int[] expected1 = {0, 1};
        int[] result1 = solution.twoSum(nums1, target1);
        if (result1.length == 2 && result1[0] == expected1[0] && result1[1] == expected1[1]) {
            score += 20;
            System.out.println("测试用例1通过");
        } else {
            System.out.println("测试用例1失败");
        }
        
        // 测试用例2: 数组中有重复元素
        int[] nums2 = {3, 2, 4, 3};
        int target2 = 6;
        int[] expected2 = {0, 3};
        int[] result2 = solution.twoSum(nums2, target2);
        if (result2.length == 2 && result2[0] == expected2[0] && result2[1] == expected2[1]) {
            score += 20;
            System.out.println("测试用例2通过");
        } else {
            System.out.println("测试用例2失败");
        }
        
        // 测试用例3: 负数和正数组合
        int[] nums3 = {-1, -2, -3, -4, -5};
        int target3 = -8;
        int[] expected3 = {2, 4};
        int[] result3 = solution.twoSum(nums3, target3);
        if (result3.length == 2 && result3[0] == expected3[0] && result3[1] == expected3[1]) {
            score += 20;
            System.out.println("测试用例3通过");
        } else {
            System.out.println("测试用例3失败");
        }
        
        // 测试用例4: 只有两个元素
        int[] nums5 = {1, 2};
        int target5 = 3;
        int[] expected5 = {0, 1};
        int[] result5 = solution.twoSum(nums5, target5);
        if (result5.length == 2 && result5[0] == expected5[0] && result5[1] == expected5[1]) {
            score += 20;
            System.out.println("测试用例4通过");
        } else {
            System.out.println("测试用例4失败");
        }
        
        // 测试用例5: 无解情况
        int[] nums6 = {1, 2, 3, 4};
        int target6 = 10;
        int[] result6 = solution.twoSum(nums6, target6);
        if (result6.length == 0) {
            score += 20;
            System.out.println("测试用例5通过");
        } else {
            System.out.println("测试用例5失败");
        }
        
        // 输出最终分数
        System.out.println("score=" + score);
        
        // 打印详细测试结果
        System.out.println("\n测试结果:");
        System.out.println("总分: " + score + "/100");
        System.out.println("通过测试用例: " + (score/20) + "/" + totalTests);
    }}