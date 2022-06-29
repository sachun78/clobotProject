package com.lge.support.second.application

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lge.support.second.application.databinding.ActivityWebViewTestBinding
import com.lge.support.second.application.databinding.FragmentMainBinding

class webViewTestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityWebViewTestBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSetting(binding.webview)

        binding.webview.loadUrl("file:///android_asset/kidContent/index.html")

        binding.webEnd.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        if(binding.webview.canGoBack())
            binding.webview.goBack()
        else
            super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setSetting(webview: WebView) {
        //필수 세팅
        webview.webViewClient = MyBrowser()//MyBrowser() WebViewClient() //페이지 컨트롤을 위한 기본 세팅
        webview.settings.javaScriptEnabled = true //웹뷰에 자바스크립트 접근 허용
        webview.settings.mediaPlaybackRequiresUserGesture = false //WebView에서 미디어를 재생하기 위해 사용자 제스처가 필요한지 여부.

        //쾌적한 환경을 위한 세팅
        webview.webChromeClient = WebChromeClient() //웹뷰로 띄운 웹페이지를 컨트롤
        //webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY //padding 영역 추가없이 내용물의 안쪽에 투명하게 스크롤바 생성. (default)

        //etc..
        //webview.settings.loadsImagesAutomatically = true //이미지 자동 로드
        //webview.settings.allowFileAccess = true //파일 액세스 허용 여부
        //webview.settings.javaScriptCanOpenWindowsAutomatically = true //새 탭에서 열릴 수 있게 처리
        //webview.settings.pluginState = WebSettings.PluginState.ON //플러그인 사용 on

    }

    private class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            url?.let { view.loadUrl(it) }
            return true
        }
    }
}