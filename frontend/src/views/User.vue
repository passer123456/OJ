<template>
  <div>
    <NavBar :menu-items="userMenuItems" />
    <div class="workspace">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
import NavBar from "@/components/NavBar.vue";

const userMenuItems = [
  { index: "/user/home", label: "主页" },
  { index: "/user/problemlist", label: "题库" },
  { index: "/user/submitrecord", label: "提交记录" },
];
</script>
