<template>
  <div class="submission-list-container">
    <!-- 提交记录列表 -->
    <div class="submission-table">
      <el-table :data="submissionList" stripe style="width: 100%">
        <el-table-column prop="submitId" label="提交ID" width="120" />
        <el-table-column prop="problemId" label="题号" width="100">
          <template #default="{ row }">
            <router-link :to="`/problem/${row.problemId}`" class="problem-link">
              {{ row.problemId }}
            </router-link>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="title" label="题目" width="200">
          <template #default="{ row }">
            <router-link
              :to="`/problem/${row.problemId}`"
              class="problem-title"
            >
              {{ row.title }}
            </router-link>
          </template>
        </el-table-column> -->
        <el-table-column prop="result" label="状态" width="150">
          <template #default="{ row }">
            <el-tag
              :type="
                row.result === 'solved'
                  ? 'success'
                  : row.result === 'Pending'
                  ? 'warning'
                  : 'danger'
              "
            >
              {{ row.result }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="language" label="语言" width="120" />
        <el-table-column prop="time" label="运行时间" width="120">
          <template #default="{ row }">
            {{ row.time }}
          </template>
        </el-table-column>
        <el-table-column prop="memory" label="内存消耗" width="120">
          <template #default="{ row }">
            {{ row.memory }}
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "@/utils/filterAxios.js";
import { reactive } from "vue";

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

const submissionList = ref([]);

const loadSubmissions = async () => {
  try {
    const res = await axios.get("/record/selectPage", {
      params: {
        userId: JSON.parse(localStorage.getItem("user")).userId,
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize,
      },
    });

    pagination.total = res.data.total;
    submissionList.value = res.data.list || [];

    // 格式化时间显示
    submissionList.value = submissionList.value.map((item) => ({
      ...item,
      time: item.time || "0",
      memory: item.memory || "0",
    }));
  } catch (error) {
    console.error("加载提交记录失败:", error);
    ElMessage.error("加载提交记录失败");
  }
};

// 分页变更处理
const handleSizeChange = (newsize) => {
  pagination.pageSize = newsize;
  pagination.currentPage = 1;
  loadSubmissions();
};

const handleCurrentChange = (newpage) => {
  pagination.currentPage = newpage;
  loadSubmissions();
};

// 初始化加载
onMounted(loadSubmissions);
</script>

<style scoped>
.submission-list-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.submission-table {
  margin-bottom: 20px;
}

.problem-title,
.problem-link {
  color: var(--el-color-primary);
  text-decoration: none;
}

.problem-title:hover,
.problem-link:hover {
  text-decoration: underline;
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>
