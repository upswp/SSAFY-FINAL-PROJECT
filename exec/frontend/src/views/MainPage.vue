<template>
  <div class="main-container">
    <main-header></main-header>
    <div class="main-contents">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import MainHeader from '@/components/common/MainHeader';
export default {
  components: {
    MainHeader,
  },
};
</script>

<style lang="scss" scoped>
.main-container {
  z-index: 10;
}
.main-contents {
  padding-top: 50px;
  min-height: 100vh;
  width: 100%;
  @include flexbox;
  @include justify-content(center);
  @include mobile() {
    padding-top: 40px;
  }
}
.aside-padding {
  @include mobile() {
    display: none;
  }
}
</style>
