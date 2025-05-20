<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({
  comment: {
    type: Object,
    required: true,
    default: () => ({}),
  },
  depth: {
    type: Number,
    default: 0,
  },
  currentUserId: {
    type: Number,
    default: null,
  },
  user: {
    type: Object,
    default: () => ({}),
  },
  commentLikes: {
    type: Object,
    default: () => ({}),
  },
  replyComment: {
    type: Object,
    default: () => ({}),
  },
  replyTo: {
    type: Number,
    default: null,
  },
});

const emit = defineEmits([
  "toggle-like",
  "show-reply-form",
  "submit-reply",
  "cancel-reply",
  "delete-comment",
]);

const toggleLike = (commentId) => {
  emit("toggle-like", commentId);
};

const showReplyForm = (commentId) => {
  emit("show-reply-form", commentId);
};

const submitReply = (parentId) => {
  emit("submit-reply", parentId);
};

const cancelReply = () => {
  emit("cancel-reply");
};

const deleteComment = (commentId) => {
  emit("delete-comment", commentId);
};
</script>

<template>
  <div class="comment-item" :class="'depth-' + depth">
    <!-- 评论头部 -->
    <div class="comment-header">
      <el-avatar
        :size="depth > 0 ? 24 : 30"
        :src="$parent.getUserAvatar(comment.userId)"
      />
      <div class="comment-user-info">
        <span class="user-name">{{ $parent.getUserName(comment.userId) }}</span>
        <span class="comment-time">{{
          $parent.formatDate(comment.createTime)
        }}</span>
      </div>
    </div>

    <!-- 评论内容 -->
    <div class="comment-content">{{ comment.content }}</div>

    <!-- 评论操作 -->
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
        v-if="comment.userId === currentUserId || user.role === 'admin'"
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

    <!-- 递归渲染子评论 - 注意组件名称大小写一致 -->
    <div v-if="comment.replies && comment.replies.length" class="replies">
      <CommentItem
        v-for="reply in comment.replies"
        :key="reply.id"
        :comment="reply"
        :depth="depth + 1"
        :current-user-id="currentUserId"
        :user="user"
        :comment-likes="commentLikes"
        :reply-comment="replyComment"
        :reply-to="replyTo"
        @toggle-like="toggleLike"
        @show-reply-form="showReplyForm"
        @submit-reply="submitReply"
        @cancel-reply="cancelReply"
        @delete-comment="deleteComment"
      />
    </div>
  </div>
</template>

<style scoped>
.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item.depth-0 {
  padding-left: 0;
}

.comment-item.depth-1 {
  padding-left: 20px;
}

.comment-item.depth-2 {
  padding-left: 40px;
}

.comment-item.depth-3 {
  padding-left: 60px;
}

.comment-item.depth-4 {
  padding-left: 80px;
}

.comment-item.depth-5 {
  padding-left: 80px;
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
</style>
