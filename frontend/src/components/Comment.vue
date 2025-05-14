<template>
  <div class="comment-section">
    <h3>评论</h3>

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
        :disabled="!newComment.content.trim()"
      >
        发表评论
      </el-button>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <el-avatar :size="30" :src="getUserAvatar(comment.userId)" />
          <div class="comment-user-info">
            <span class="user-name">{{ getUserName(comment.userId) }}</span>
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
            :type="isLiked(comment.id) ? 'primary' : ''"
          >
            <el-icon><Star /></el-icon> {{ comment.likeCount }}
          </el-button>
          <el-button size="small" @click="showReplyForm(comment.id)"
            >回复</el-button
          >
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
            <el-button size="small" @click="cancelReply">取消</el-button>
          </div>
        </div>

        <!-- 子评论 -->
        <div v-if="comment.replies && comment.replies.length" class="replies">
          <div
            v-for="reply in comment.replies"
            :key="reply.id"
            class="reply-item"
          >
            <div class="comment-header">
              <el-avatar :size="24" :src="getUserAvatar(reply.userId)" />
              <div class="comment-user-info">
                <span class="user-name">{{ getUserName(reply.userId) }}</span>
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
                :type="isLiked(reply.id) ? 'primary' : ''"
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
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { Star } from "@element-plus/icons-vue";
import axios from "@/utils/filterAxios.js";
import { ElMessage } from "element-plus";
import dayjs from "dayjs";

const props = defineProps({
  problemId: {
    type: Number,
    required: true,
  },
  currentUserId: {
    type: Number,
    required: true,
  },
  userList: {
    type: Array,
    default: () => [],
  },
});

// 评论数据
const comments = ref([]);
const newComment = ref({
  problemId: props.problemId,
  userId: props.currentUserId,
  content: "",
  parentId: null,
});
const replyComment = ref({
  problemId: props.problemId,
  userId: props.currentUserId,
  content: "",
  parentId: null,
});
const replyTo = ref(null);
const likedComments = ref([]);

// 获取用户头像
const getUserAvatar = (userId) => {
  const user = props.userList.find((u) => u.userId === userId);
  return (
    user?.avatar ||
    "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
  );
};

// 获取用户名
const getUserName = (userId) => {
  const user = props.userList.find((u) => u.userId === userId);
  return user?.username || `用户${userId}`;
};

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format("YYYY-MM-DD HH:mm");
};

// 检查是否已点赞
const isLiked = (commentId) => {
  return likedComments.value.includes(commentId);
};

// 获取评论
const fetchComments = async () => {
  try {
    const response = await axios.get(
      `/comments/problem/${props.problemId}/tree`
    );
    comments.value = response.data || [];
  } catch (error) {
    ElMessage.error("获取评论失败");
    console.error("获取评论失败:", error);
  }
};

// 提交评论
const submitComment = async () => {
  if (!newComment.value.content.trim()) return;

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
    await axios.delete(`/comments/${commentId}?userId=${props.currentUserId}`);

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
  try {
    if (isLiked(commentId)) {
      await axios.post(`/comments/${commentId}/unlike`);
      likedComments.value = likedComments.value.filter(
        (id) => id !== commentId
      );
    } else {
      await axios.post(`/comments/${commentId}/like`);
      likedComments.value.push(commentId);
    }

    // 更新点赞数
    updateCommentLikeCount(commentId, isLiked(commentId) ? -1 : 1);
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

// 更新点赞数
const updateCommentLikeCount = (id, increment) => {
  const updateCount = (comments) => {
    for (const comment of comments) {
      if (comment.id === id) {
        comment.likeCount += increment;
        return true;
      }
      if (comment.replies && updateCount(comment.replies)) {
        return true;
      }
    }
    return false;
  };
  updateCount(comments.value);
};

onMounted(() => {
  fetchComments();

  // 加载用户点赞的评论
  if (props.currentUserId) {
    axios
      .get(
        `/comments/liked?userId=${props.currentUserId}&problemId=${props.problemId}`
      )
      .then((response) => {
        likedComments.value = response.data || [];
      })
      .catch((error) => {
        console.error("获取点赞信息失败:", error);
      });
  }
});
</script>

<style scoped>
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
