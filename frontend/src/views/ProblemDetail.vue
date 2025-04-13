<template>
  <div class="leetcode-container">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="logo">Online Judge</div>
      <div class="user-info">
        <el-dropdown>
          <span class="el-dropdown-link">
            <el-avatar :size="30" :src="user.avatar" />
            {{ user.username }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧题目区域 -->
      <div class="problem-panel">
        <!-- 题目头部 -->
        <div class="problem-header">
          <h2>{{ problem.title }}</h2>
          <div class="meta-info">
            <el-tag :type="difficultyTagType">{{ difficultyText }}</el-tag>
            <span class="accept-rate">通过率: {{ acceptanceRate }}%</span>
          </div>
        </div>

        <!-- 标签切换 -->
        <el-tabs v-model="activeTab" class="problem-tabs">
          <el-tab-pane label="题目详情" name="description">
            <div class="problem-content" v-html="formattedDescription"></div>

            <!-- 示例 -->
            <div
              class="examples"
              v-for="(example, index) in examples"
              :key="index"
            >
              <h3>示例 {{ index + 1 }}</h3>
              <div class="example-io">
                <div class="input">
                  <strong>输入:</strong>
                  <pre>{{ example.input }}</pre>
                </div>
                <div class="output">
                  <strong>输出:</strong>
                  <pre>{{ example.output }}</pre>
                </div>
              </div>
              <div class="explanation" v-if="example.explanation">
                <strong>解释:</strong>
                <p>{{ example.explanation }}</p>
              </div>
            </div>

            <!-- 约束条件 -->
            <div class="constraints" v-if="constraints.length > 0">
              <h3>约束条件:</h3>
              <ul>
                <li v-for="(constraint, index) in constraints" :key="index">
                  {{ constraint }}
                </li>
              </ul>
            </div>
          </el-tab-pane>

          <el-tab-pane label="提交记录" name="submissions">
            <el-table :data="submissions" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="status" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="getStatusTagType(row.status)">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="language" label="语言" width="100" />
              <el-table-column prop="runtime" label="执行用时" width="100" />
              <el-table-column prop="memory" label="内存消耗" width="100" />
              <el-table-column prop="submitTime" label="提交时间" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="讨论区" name="discussions">
            <div class="discussion-list">
              <div
                v-for="discussion in discussions"
                :key="discussion.id"
                class="discussion-item"
              >
                <div class="discussion-header">
                  <el-avatar :size="30" :src="discussion.author.avatar" />
                  <span class="author-name">{{ discussion.author.name }}</span>
                  <span class="post-time">{{ discussion.time }}</span>
                </div>
                <h4 class="discussion-title">{{ discussion.title }}</h4>
                <p class="discussion-content">{{ discussion.content }}</p>
                <div class="discussion-footer">
                  <span class="like-count">
                    <el-icon><Star /></el-icon> {{ discussion.likes }}
                  </span>
                  <span class="comment-count">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ discussion.comments }}
                  </span>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 右侧代码编辑区 -->
      <div class="editor-panel">
        <!-- 语言选择 -->
        <div class="editor-header">
          <el-select
            v-model="currentLanguage"
            placeholder="选择语言"
            @change="handleLanguageChange"
          >
            <el-option
              v-for="lang in languages"
              :key="lang.value"
              :label="lang.label"
              :value="lang.value"
            />
          </el-select>
          <div class="editor-actions">
            <el-button size="small" @click="resetCode">重置</el-button>
            <el-button size="small" @click="openSettings">
              <el-icon><Setting /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 代码编辑器 -->
        <div class="code-editor">
          <textarea ref="editor" v-model="code" spellcheck="false"></textarea>
        </div>

        <!-- 测试用例 -->
        <div class="test-case">
          <el-collapse v-model="activeTestCases">
            <el-collapse-item title="测试用例" name="test-case">
              <el-input
                v-model="testInput"
                type="textarea"
                :rows="3"
                placeholder="输入测试用例"
              />
            </el-collapse-item>
          </el-collapse>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" @click="runTest">执行代码</el-button>
          <el-button type="success" @click="submitSolution">提交</el-button>
        </div>

        <!-- 执行结果 -->
        <div class="execution-result" v-if="executionResult">
          <h3>执行结果：</h3>
          <div class="result-content">
            <div v-if="executionResult.status === 'success'">
              <el-tag type="success">通过</el-tag>
              <p>执行用时: {{ executionResult.runtime }} ms</p>
              <p>内存消耗: {{ executionResult.memory }} MB</p>
            </div>
            <div v-else>
              <el-tag type="danger">错误</el-tag>
              <p>{{ executionResult.message }}</p>
              <pre v-if="executionResult.error">{{
                executionResult.error
              }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { Star, ChatDotRound, Setting } from "@element-plus/icons-vue";
import axios from "@/utils/filterAxios.js";

const route = useRoute();

// 用户信息
const user = ref({
  username: "当前用户",
  avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
});

// 题目数据
const problem = ref({
  id: 1,
  title: "两数之和",
  description: `给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。

你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。

你可以按任意顺序返回答案。

**示例 1：**

\`\`\`
输入：nums = [2,7,11,15], target = 9
输出：[0,1]
解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
\`\`\`

**示例 2：**

\`\`\`
输入：nums = [3,2,4], target = 6
输出：[1,2]
\`\`\`

**示例 3：**

\`\`\`
输入：nums = [3,3], target = 6
输出：[0,1]
\`\`\`

**提示：**

- 2 <= nums.length <= 10^4
- -10^9 <= nums[i] <= 10^9
- -10^9 <= target <= 10^9
- 只会存在一个有效答案`,
  difficulty: "medium",
  submitCount: 12543,
  acceptCount: 8765,
  sampleInput: "nums = [2,7,11,15]\ntarget = 9",
  sampleOutput: "[0,1]",
});

// 标签页
const activeTab = ref("description");

// 难度标签
const difficultyTagType = computed(() => {
  switch (problem.value.difficulty) {
    case "easy":
      return "success";
    case "medium":
      return "warning";
    case "hard":
      return "danger";
    default:
      return "";
  }
});

const difficultyText = computed(() => {
  switch (problem.value.difficulty) {
    case "easy":
      return "简单";
    case "medium":
      return "中等";
    case "hard":
      return "困难";
    default:
      return problem.value.difficulty;
  }
});

// 通过率
const acceptanceRate = computed(() => {
  if (problem.value.submitCount === 0) return 0;
  return Math.round(
    (problem.value.acceptCount / problem.value.submitCount) * 100
  );
});

// 格式化题目描述
const formattedDescription = computed(() => {
  return problem.value.description
    .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
    .replace(/\n/g, "<br>")
    .replace(/```([\s\S]*?)```/g, "<pre><code>$1</code></pre>");
});

// 示例数据
const examples = computed(() => {
  const inputs = problem.value.sampleInput.split("\n");
  const outputs = problem.value.sampleOutput.split("\n");
  return inputs.map((input, index) => ({
    input: input.trim(),
    output: outputs[index] ? outputs[index].trim() : "",
    explanation: "",
  }));
});

// 约束条件
const constraints = computed(() => {
  const matches = problem.value.description.match(/- (.*?)(?=\n\n|$)/g);
  if (matches) {
    return matches.map((m) => m.replace(/^- /, ""));
  }
  return [];
});

// 提交记录
const submissions = ref([
  {
    id: 1,
    status: "通过",
    language: "Java",
    runtime: "2 ms",
    memory: "41.5 MB",
    submitTime: "2023-05-15 14:30",
  },
  {
    id: 2,
    status: "解答错误",
    language: "Python",
    runtime: "N/A",
    memory: "N/A",
    submitTime: "2023-05-14 09:15",
  },
  {
    id: 3,
    status: "执行出错",
    language: "C++",
    runtime: "N/A",
    memory: "N/A",
    submitTime: "2023-05-13 16:45",
  },
]);

const getStatusTagType = (status) => {
  switch (status) {
    case "通过":
      return "success";
    case "解答错误":
      return "danger";
    case "执行出错":
      return "warning";
    default:
      return "info";
  }
};

// 讨论区
const discussions = ref([
  {
    id: 1,
    author: {
      name: "用户A",
      avatar:
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    },
    time: "2天前",
    title: "这个问题的最优解是什么？",
    content:
      "我尝试了几种方法，但都不是最优解，有没有人能分享一下更高效的解法？",
    likes: 24,
    comments: 8,
  },
  {
    id: 2,
    author: {
      name: "用户B",
      avatar:
        "https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png",
    },
    time: "1周前",
    title: "关于边界条件的疑问",
    content: "当输入为空数组时，应该返回什么？题目描述中没有明确说明这种情况。",
    likes: 15,
    comments: 5,
  },
]);

// 代码编辑器相关
const languages = ref([
  { value: "java", label: "Java" },
  { value: "python", label: "Python" },
  { value: "cpp", label: "C++" },
  { value: "javascript", label: "JavaScript" },
]);
const currentLanguage = ref("java");
const code = ref("");
const testInput = ref("");
const activeTestCases = ref(["test-case"]);
const executionResult = ref(null);

// 默认代码模板
const codeTemplates = {
  java: `class Solution {
    public int[] twoSum(int[] nums, int target) {
        // 你的代码
    }
}`,
  python: `class Solution:
    def twoSum(self, nums: List[int], target: int) -> List[int]:
        # 你的代码`,
  cpp: `class Solution {
public:
    vector<int> twoSum(vector<int>& nums, int target) {
        // 你的代码
    }
};`,
  javascript: `/**
 * @param {number[]} nums
 * @param {number} target
 * @return {number[]}
 */
var twoSum = function(nums, target) {
    // 你的代码
};`,
};

// 初始化代码编辑器
onMounted(() => {
  resetCode();
});

// 切换语言
const handleLanguageChange = (lang) => {
  code.value = codeTemplates[lang];
};

// 重置代码
const resetCode = () => {
  code.value = codeTemplates[currentLanguage.value];
};

// 打开设置
const openSettings = () => {
  console.log("打开编辑器设置");
};

// 执行测试
const runTest = async () => {
  if (!testInput.value.trim()) {
    ElMessage.warning("请输入测试用例");
    return;
  }

  executionResult.value = null;
  try {
    // 模拟API调用
    await new Promise((resolve) => setTimeout(resolve, 1000));

    executionResult.value = {
      status: "success",
      runtime: Math.floor(Math.random() * 10) + 1,
      memory: (Math.random() * 2 + 38).toFixed(1),
      message: "测试用例通过",
    };
  } catch (error) {
    executionResult.value = {
      status: "error",
      message: "执行出错",
      error: error.message,
    };
  }
};

// 提交代码
const submitSolution = async () => {
  if (!code.value.trim()) {
    ElMessage.warning("请输入代码");
    return;
  }

  try {
    // 模拟API调用
    await new Promise((resolve) => setTimeout(resolve, 1500));

    ElMessage.success("提交成功");

    // 添加到提交记录
    submissions.value.unshift({
      id: submissions.value.length + 1,
      status: Math.random() > 0.3 ? "通过" : "解答错误",
      language: currentLanguage.value,
      runtime:
        Math.random() > 0.3
          ? `${Math.floor(Math.random() * 10) + 1} ms`
          : "N/A",
      memory:
        Math.random() > 0.3
          ? `${(Math.random() * 2 + 38).toFixed(1)} MB`
          : "N/A",
      submitTime: new Date().toLocaleString(),
    });

    executionResult.value = null;
  } catch (error) {
    ElMessage.error("提交失败: " + error.message);
  }
};
</script>

<style scoped>
.leetcode-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 50px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.el-dropdown-link .el-avatar {
  margin-right: 8px;
}

.main-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.problem-panel {
  flex: 1;
  padding: 20px;
  background-color: #fff;
  overflow-y: auto;
  border-right: 1px solid #e8e8e8;
}

.editor-panel {
  width: 45%;
  min-width: 500px;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.problem-header {
  margin-bottom: 20px;
}

.problem-header h2 {
  margin: 0 0 10px 0;
  font-size: 22px;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.accept-rate {
  color: #666;
  font-size: 14px;
}

.problem-tabs {
  margin-top: 15px;
}

.problem-content {
  line-height: 1.6;
  margin-bottom: 20px;
}

.problem-content :deep(pre) {
  background-color: #f6f8fa;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
}

.examples {
  margin: 20px 0;
}

.example-io {
  display: flex;
  gap: 20px;
  margin: 10px 0;
}

.example-io > div {
  flex: 1;
}

.example-io pre {
  background-color: #f6f8fa;
  padding: 10px;
  border-radius: 4px;
  margin-top: 5px;
}

.constraints ul {
  padding-left: 20px;
}

.constraints li {
  margin-bottom: 5px;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #e8e8e8;
}

.editor-actions {
  display: flex;
  gap: 8px;
}

.code-editor {
  flex: 1;
  position: relative;
}

.code-editor textarea {
  width: 100%;
  height: 100%;
  padding: 15px;
  font-family: "Monaco", "Menlo", "Ubuntu Mono", monospace;
  font-size: 14px;
  line-height: 1.5;
  border: none;
  resize: none;
  outline: none;
}

.test-case {
  padding: 0 15px;
  border-top: 1px solid #e8e8e8;
}

.action-buttons {
  padding: 10px 15px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid #e8e8e8;
}

.execution-result {
  padding: 15px;
  border-top: 1px solid #e8e8e8;
}

.execution-result h3 {
  margin-top: 0;
}

.result-content {
  margin-top: 10px;
}

.result-content pre {
  background-color: #f6f8fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}

.discussion-list {
  margin-top: 15px;
}

.discussion-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.discussion-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.discussion-header .el-avatar {
  margin-right: 10px;
}

.author-name {
  font-weight: 500;
  margin-right: 10px;
}

.post-time {
  color: #999;
  font-size: 12px;
}

.discussion-title {
  margin: 5px 0;
  font-size: 16px;
}

.discussion-content {
  color: #333;
  margin: 8px 0;
}

.discussion-footer {
  display: flex;
  gap: 15px;
  color: #666;
  font-size: 12px;
}

.discussion-footer .el-icon {
  margin-right: 3px;
}
</style>
<style lang="scss" scoped></style>
