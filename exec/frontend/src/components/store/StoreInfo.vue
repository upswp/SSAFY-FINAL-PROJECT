<template>
  <div v-if="loaded" class="store-info-container">
    <label for="thumbnail-image-upload">
      <img class="store-thumbnail-image" :src="thumbImg" />
      <input id="thumbnail-image-upload" type="file" class="thumbnail-image-upload" @change="submitThumbnailImage" />
    </label>
    <basic-info v-bind="baseInfo" :logo="sideInfo.logo"></basic-info>
    <additional-info :info="sideInfo" :store-id="storeId"></additional-info>
  </div>
</template>

<script>
import BasicInfo from '@/components/store/info/BasicInfo';
import AdditionalInfo from '@/components/store/info/AdditionalInfo';

import { getStoreInfo, updateStoreSideInfo } from '@/api/store';
import { mapMutations } from 'vuex';
export default {
  components: {
    BasicInfo,
    AdditionalInfo,
  },
  data() {
    return {
      loaded: false,
      baseInfo: '',
      sideInfo: '',
      thumbImg: require('@/assets/image/thumbnail_example.jpg'),
      thumbnailFile: '',
      storeId: '',
    };
  },
  async created() {
    try {
      this.setSpinnerState(true);
      const { data } = await getStoreInfo();
      const { baseInfo, sideInfo } = data;
      this.baseInfo = baseInfo;
      this.sideInfo = sideInfo;
      this.storeId = baseInfo.storeId;
      if (sideInfo.thumbImg) this.thumbImg = sideInfo.thumbImg;
      this.loaded = true;
    } catch (error) {
      console.log(error);
      alert('가게 정보 불러오기를 실패했습니다');
    } finally {
      this.setSpinnerState(false);
    }
  },
  methods: {
    ...mapMutations(['setSpinnerState']),
    async submitThumbnailImage(e) {
      const file = e.target.files[0];
      this.thumbnailFile = file;
      try {
        const frm = new FormData();
        frm.append('thumbImg', this.thumbnailFile);
        frm.append('storeId', this.storeId);
        this.setSpinnerState(true);
        await updateStoreSideInfo(frm);
        this.thumbImg = URL.createObjectURL(file);
      } catch (error) {
        console.log(error);
        alert('썸네일 업로드에 실패하였습니다');
      } finally {
        this.setSpinnerState(false);
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.store-info-container {
  width: 80%;
  border: 1px solid $gray200;
  @include mobile {
    width: 100%;
  }
  @include xs-mobile {
    width: 100%;
  }
}
.store-thumbnail-image {
  width: 100%;
  height: 10vw;
  object-fit: cover;
  object-position: center 50%;
  cursor: pointer;
}
.thumbnail-image-upload {
  display: none;
}
</style>
