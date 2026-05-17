import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  // 增加下面这段 server 配置，让 Vite 充当中间人
  server: {
    proxy: {
      '/api': {
        target: process.env.VITE_API_PROXY_TARGET || 'http://localhost:8080', // 你的 Spring Boot 后端地址
        changeOrigin: true // 欺骗后端，假装是同源请求
      }
    }
  }
})
