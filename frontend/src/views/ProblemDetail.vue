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
              <el-dropdown-item @click="handleProfileClick"
                >个人中心</el-dropdown-item
              >
              <el-dropdown-item @click="handleLogout"
                >退出登录</el-dropdown-item
              >
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
              <el-table-column prop="submitId" label="ID" width="80" />
              <el-table-column prop="result" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="getStatusTagType(row.result)">
                    {{ row.result }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="language" label="语言" width="100" />
              <el-table-column prop="time" label="执行用时" width="100" />
              <el-table-column prop="memory" label="内存消耗" width="100" />
              <el-table-column prop="submitTime" label="提交时间" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="讨论区" name="discussions">
            <div class="comment-section">
              <!-- 评论表单 -->
              <div class="comment-form">
                <el-input
                  v-model="newComment.content"
                  type="textarea"
                  :rows="3"
                  placeholder="写下你的评论..."
                />
                <el-button
                  type="primary"
                  @click="submitComment"
                  :disabled="!newComment.content.trim() || !currentUserId"
                >
                  发表评论
                </el-button>
              </div>

              <!-- 评论列表 -->
              <div class="comment-list">
                <div
                  v-for="comment in comments"
                  :key="comment.id"
                  class="comment-item"
                >
                  <div class="comment-header">
                    <el-avatar
                      :size="30"
                      :src="getUserAvatar(comment.userId)"
                    />
                    <div class="comment-user-info">
                      <span class="user-name">{{
                        getUserName(comment.userId)
                      }}</span>
                      <span class="comment-time">{{
                        formatDate(comment.createTime)
                      }}</span>
                    </div>
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                  <div class="comment-actions">
                    <el-button
                      size="small"
                      @click="toggleLike(comment.id)"
                      :type="commentLikes[comment.id] ? 'primary' : ''"
                      :disabled="!currentUserId"
                    >
                      <el-icon><Star /></el-icon> {{ comment.likeCount }}
                    </el-button>
                    <el-button
                      size="small"
                      @click="showReplyForm(comment.id)"
                      :disabled="!currentUserId"
                    >
                      回复
                    </el-button>
                    <el-button
                      v-if="comment.userId === currentUserId"
                      size="small"
                      type="danger"
                      @click="deleteComment(comment.id)"
                    >
                      删除
                    </el-button>
                  </div>

                  <!-- 回复表单 -->
                  <div v-if="replyTo === comment.id" class="reply-form">
                    <el-input
                      v-model="replyComment.content"
                      type="textarea"
                      :rows="2"
                      placeholder="写下你的回复..."
                    />
                    <div class="reply-actions">
                      <el-button
                        size="small"
                        @click="submitReply(comment.id)"
                        :disabled="!replyComment.content.trim()"
                      >
                        回复
                      </el-button>
                      <el-button size="small" @click="cancelReply"
                        >取消</el-button
                      >
                    </div>
                  </div>

                  <!-- 子评论 -->
                  <div
                    v-if="comment.replies && comment.replies.length"
                    class="replies"
                  >
                    <div
                      v-for="reply in comment.replies"
                      :key="reply.id"
                      class="reply-item"
                    >
                      <div class="comment-header">
                        <el-avatar
                          :size="24"
                          :src="getUserAvatar(reply.userId)"
                        />
                        <div class="comment-user-info">
                          <span class="user-name">{{
                            getUserName(reply.userId)
                          }}</span>
                          <span class="comment-time">{{
                            formatDate(reply.createTime)
                          }}</span>
                        </div>
                      </div>
                      <div class="comment-content">{{ reply.content }}</div>
                      <div class="comment-actions">
                        <el-button
                          size="small"
                          @click="toggleLike(reply.id)"
                          :type="commentLikes[reply.id] ? 'primary' : ''"
                          :disabled="!currentUserId"
                        >
                          <el-icon><Star /></el-icon> {{ reply.likeCount }}
                        </el-button>
                        <el-button
                          v-if="reply.userId === currentUserId"
                          size="small"
                          type="danger"
                          @click="deleteComment(reply.id)"
                        >
                          删除
                        </el-button>
                      </div>
                    </div>
                  </div>
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
        <el-dialog
          v-model="showResultDialog"
          title="提交结果"
          width="70%"
          top="5vh"
          destroy-on-close
        >
          <div v-if="submissionResult" class="result-dialog-content">
            <!-- 用户代码 -->
            <div class="code-section">
              <h3>你的代码</h3>
              <pre class="code-block">{{ code }}</pre>
            </div>

            <!-- 执行结果 -->
            <div class="result-section">
              <h3>执行结果</h3>

              <div class="status-info">
                <el-tag
                  :type="
                    submissionResult.errno === 0
                      ? 'success'
                      : submissionResult.errno === 1
                      ? 'danger'
                      : 'warning'
                  "
                >
                  {{
                    submissionResult.errno === 0
                      ? "编译通过"
                      : submissionResult.errno === 1
                      ? "编译错误"
                      : "运行时错误"
                  }}
                </el-tag>

                <div class="metrics">
                  <span v-if="submissionResult.score" class="metric">
                    <strong>得分:</strong> {{ submissionResult.score }}
                  </span>
                  <span v-if="submissionResult.memory" class="metric">
                    <strong>内存消耗:</strong>
                    {{ submissionResult.memory }}
                  </span>
                  <span v-if="submissionResult.runtime" class="metric">
                    <strong>运行时间:</strong>
                    {{ submissionResult.runtime }}
                  </span>
                </div>
              </div>

              <!-- 输出内容 -->
              <div
                v-if="
                  submissionResult.compile_error ||
                  submissionResult.stdout ||
                  submissionResult.stderr
                "
                class="output-content"
              >
                <div v-if="submissionResult.errno === 1" class="compile-error">
                  <h4>编译错误信息</h4>
                  <pre>{{ submissionResult.compile_error }}</pre>
                </div>

                <template v-else>
                  <div v-if="submissionResult.stdout" class="stdout">
                    <h4>标准输出</h4>
                    <pre>{{ submissionResult.stdout }}</pre>
                  </div>

                  <div v-if="submissionResult.stderr" class="stderr">
                    <h4>错误输出</h4>
                    <pre>{{ submissionResult.stderr }}</pre>
                  </div>
                </template>
              </div>
            </div>
          </div>

          <template #footer>
            <el-button type="primary" @click="showResultDialog = false"
              >关闭</el-button
            >
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { Star, ChatDotRound, Setting } from "@element-plus/icons-vue";
import axios from "@/utils/filterAxios.js";
import { ElMessage } from "element-plus";
import dayjs from "dayjs";

const router = useRouter();
const route = useRoute();

// 用户数据
const user = ref({
  username: computed(() => {
    const storedUser = JSON.parse(localStorage.getItem("user")) || {
      username: "未登录",
    };
    return storedUser.username;
  }),
  avatar: computed(() => {
    const storedUser = JSON.parse(localStorage.getItem("user")) || {};
    return (
      storedUser.avatar ||
      "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
    );
  }),
  currentUserId: computed(() => {
    const storedUser = JSON.parse(localStorage.getItem("user")) || {};
    return storedUser.userId || null;
  }),
});
const currentUserId = computed(() => user.value.currentUserId);

// 处理个人中心点击
const handleProfileClick = () => {
  router.push("/profile"); // 跳转到个人中心页面
};

//登出
const handleLogout = () => {
  localStorage.removeItem("user");
  location.href = "/login";
};

// 题目数据
const problem = ref({
  id: null,
  title: "",
  description: ``,
  difficulty: "",
  submitCount: 0,
  acceptCount: 0,
  sampleInput: "",
  sampleOutput: "",
  templateCode: "",
  testCode: "",
});

const fetchProblem = async (id) => {
  try {
    const res = await axios.get(`/problem/${id}`);
    problem.value = res.data;
    // 加载提交记录
    await loadSubmissions(id);

    // 如果有提交记录，使用最新记录的代码
    if (submissions.value.length > 0) {
      code.value = latestSubmission.value.code;
    } else {
      resetCode(); // 没有记录则使用默认模板
    }
  } catch (err) {
    ElMessage.error("题目加载失败");
  }
};

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
const submissions = ref([{}]);
const showResultDialog = ref(false);
const submissionResult = ref({
  errno: 0,
  compile_error: null,
  stdout: null,
  stderr: null,
  score: 0,
  memory: null,
  runtime: null,
});

const latestSubmission = computed(() => {
  if (!submissions.value || submissions.value.length === 0) return null;

  // 过滤掉没有submitTime的记录
  const validSubmissions = submissions.value.filter((sub) => sub.submitTime);

  if (validSubmissions.length === 0) return null;

  // 按提交时间降序排序
  const sorted = [...validSubmissions].sort((a, b) => {
    return new Date(b.submitTime) - new Date(a.submitTime);
  });

  return sorted[0];
});

const loadSubmissions = async (problemId) => {
  try {
    const userId = JSON.parse(localStorage.getItem("user"))?.userId || null;
    const res = await axios.get("/record/select", {
      params: {
        userId,
        problemId,
      },
    });
    submissions.value = res.data || [];
  } catch (error) {
    console.error("加载提交记录失败:", error);
    ElMessage.error("加载提交记录失败");
  }
};

const getStatusTagType = (status) => {
  switch (status) {
    case "solved":
      return "success";
    case "unsolved":
      return "danger";
    default:
      return "info";
  }
};

// 讨论区
const comments = ref([]);
const newComment = ref({
  problemId: Number(route.params.id),
  userId: currentUserId.value,
  content: "",
  parentId: null,
});
const replyComment = ref({
  problemId: Number(route.params.id),
  userId: currentUserId.value,
  content: "",
  parentId: null,
});
const replyTo = ref(null);
const commentLikes = ref({});

// 获取用户信息
const getUserAvatar = (userId) => {
  // 实际项目中应从用户列表获取
  return "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";
};

const getUserName = (userId) => {
  // 实际项目中应从用户列表获取
  return `用户${userId}`;
};

const formatDate = (date) => {
  return dayjs(date).format("YYYY-MM-DD HH:mm");
};

// 检查是否已点赞
const isLiked = async (commentId) => {
  const response = await axios.get(
    `/comments/${commentId}/isliked?userId=${currentUserId.value}`
  );
  isLiked.value = response.data;
  console.log("点赞状态:", response.data);
  return isLiked.value;
};

const checkLikeStatus = async (commentId) => {
  try {
    const response = await axios.get(
      `/comments/${commentId}/isliked?userId=${currentUserId.value}`
    );
    return response.data;
  } catch (error) {
    console.error("检查点赞状态失败:", error);
    return false;
  }
};

// 获取评论
const fetchComments = async () => {
  try {
    const response = await axios.get(`/comments/problem/${route.params.id}`);
    comments.value = response.data || [];
    // 并行加载所有点赞状态
    await Promise.all(
      comments.value.map(async (comment) => {
        commentLikes.value[comment.id] = await checkLikeStatus(comment.id);
        if (comment.replies) {
          await Promise.all(
            comment.replies.map((reply) => {
              commentLikes.value[reply.id] = checkLikeStatus(reply.id);
            })
          );
        }
      })
    );
  } catch (error) {
    ElMessage.error("获取评论失败");
  }
};

// 提交评论
const submitComment = async () => {
  if (!newComment.value.content.trim() || !currentUserId.value) return;

  try {
    const response = await axios.post("/comments", newComment.value);
    comments.value.unshift(response.data);
    newComment.value.content = "";
    ElMessage.success("评论发表成功");
  } catch (error) {
    ElMessage.error("发表评论失败");
    console.error("发表评论失败:", error);
  }
};

// 显示回复表单
const showReplyForm = (commentId) => {
  if (!currentUserId.value) return;
  replyTo.value = commentId;
  replyComment.value.parentId = commentId;
};

// 取消回复
const cancelReply = () => {
  replyTo.value = null;
  replyComment.value.content = "";
  replyComment.value.parentId = null;
};

// 提交回复
const submitReply = async (parentId) => {
  if (!replyComment.value.content.trim()) return;

  try {
    const response = await axios.post("/comments", replyComment.value);

    // 找到父评论并添加回复
    const parentComment = findComment(comments.value, parentId);
    if (parentComment) {
      if (!parentComment.replies) {
        parentComment.replies = [];
      }
      parentComment.replies.push(response.data);
    }

    cancelReply();
    ElMessage.success("回复成功");
  } catch (error) {
    ElMessage.error("回复失败");
    console.error("回复失败:", error);
  }
};

// 删除评论
const deleteComment = async (commentId) => {
  try {
    await axios.delete(`/comments/${commentId}?userId=${currentUserId.value}`);

    // 从评论列表中移除
    removeComment(comments.value, commentId);
    ElMessage.success("评论已删除");
  } catch (error) {
    ElMessage.error("删除评论失败");
    console.error("删除评论失败:", error);
  }
};

// 点赞/取消点赞
const toggleLike = async (commentId) => {
  if (!currentUserId.value) return;

  try {
    const currentLikeStatus = commentLikes.value[commentId];

    // 立即更新UI状态（乐观更新）
    commentLikes.value[commentId] = !currentLikeStatus;
    updateCommentLikeCount(commentId, currentLikeStatus ? -1 : 1);
    if (currentLikeStatus) {
      await axios.delete(
        `/comments/${commentId}/unlike?userId=${currentUserId.value}`
      );
    } else {
      await axios.post(
        `/comments/${commentId}/like?userId=${currentUserId.value}`
      );
    }

    // await fetchComments();
  } catch (error) {
    ElMessage.error("操作失败");
    console.error("点赞操作失败:", error);
  }
};

// 递归查找评论
const findComment = (comments, id) => {
  for (const comment of comments) {
    if (comment.id === id) return comment;
    if (comment.replies) {
      const found = findComment(comment.replies, id);
      if (found) return found;
    }
  }
  return null;
};

// 递归移除评论
const removeComment = (comments, id) => {
  for (let i = 0; i < comments.length; i++) {
    if (comments[i].id === id) {
      comments.splice(i, 1);
      return true;
    }
    if (comments[i].replies) {
      if (removeComment(comments[i].replies, id)) {
        return true;
      }
    }
  }
  return false;
};

// 递归更新点赞数
const updateCommentLikeCount = (commentId, increment) => {
  const updateFn = (comments) => {
    for (const comment of comments) {
      if (comment.id === commentId) {
        comment.likeCount += increment;
        return true;
      }
      if (comment.replies?.length) {
        if (updateFn(comment.replies)) return true;
      }
    }
    return false;
  };
  updateFn(comments.value);
};

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

// 切换语言
const handleLanguageChange = (lang) => {
  currentLanguage.value = lang;
};

// 重置代码
const resetCode = () => {
  code.value =
    problem.value.templateCode || codeTemplates[currentLanguage.value];
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
    const request = {
      id: problem.value.problemid, // 题目ID
      userId: JSON.parse(localStorage.getItem("user"))?.userId || null, // 用户ID
      language: currentLanguage.value, // 当前选择的语言
      testCode: problem.value.testCode, // 题目测试代码
      userCode: code.value, // 用户代码（与code字段相同）
    };

    const response = await axios.post("/record/submit", request);

    console.log("提交结果:", response.data);

    if (response.code == 200) {
      ElMessage.success("提交成功");
      submissionResult.value = response.data;
      showResultDialog.value = true;
      loadSubmissions(problem.value.problemid); // 重新加载提交记录
    }
    executionResult.value = null;
  } catch (error) {
    ElMessage.error("提交失败: " + error.message);
  }
};

watch(
  () => route.path,
  (path) => {
    activeIndex.value = path;
  }
);

watch(
  () => Number(route.params.id),
  (newId) => {
    if (newId) fetchProblem(newId);
  },
  { immediate: true }
);

onMounted(() => {
  const problemId = Number(route.params.id);
  if (problemId) {
    fetchProblem(problemId);
    fetchComments();
  } else {
    resetCode();
  }
});
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

.result-dialog-content {
  max-height: 70vh;
  overflow-y: auto;
}

.code-section,
.result-section {
  margin-bottom: 20px;
}

.code-section h3,
.result-section h3 {
  margin-top: 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.code-block,
pre {
  background-color: #f6f8fa;
  padding: 12px;
  border-radius: 4px;
  white-space: pre-wrap;
  font-family: "Courier New", monospace;
  line-height: 1.5;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
}

.metrics {
  display: flex;
  gap: 15px;
}

.metric {
  font-size: 14px;
}

.output-content h4 {
  margin: 15px 0 8px 0;
  font-size: 15px;
}

.compile-error pre {
  background-color: #fef0f0;
  color: #f56c6c;
}

.stderr pre {
  background-color: #fef0f0;
  color: #f56c6c;
}

.comment-section {
  margin-top: 20px;
}

.comment-form {
  margin-bottom: 20px;
}

.comment-form .el-button {
  margin-top: 10px;
}

.comment-list {
  margin-top: 15px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.comment-user-info {
  margin-left: 10px;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin: 8px 0;
  font-size: 14px;
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.comment-actions .el-button {
  padding: 5px 8px;
  font-size: 12px;
}

.reply-form {
  margin: 10px 0 10px 40px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.replies {
  margin-left: 40px;
  border-left: 2px solid #eee;
  padding-left: 10px;
}

.reply-item {
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-item .comment-header {
  margin-bottom: 5px;
}

.reply-item .comment-content {
  margin: 5px 0;
  font-size: 13px;
}

.reply-item .comment-actions {
  margin-top: 5px;
}
</style>
<style lang="scss" scoped></style>
