# ScreenRecordLibrary
本库是基于MediaProjection封装的手机屏幕录制开源库，并提交到Jcenter，方便大家使用

使用方法： 

    module中的build.gradle中的depandencies中添加依赖即可，如下
    dependencies {
    xxxxxx
    compile 'com.guaju:screenrecorderlibrary:1.0.1'
    }
  
  目前最新的版本是1.0.1，仅仅是朋友用了用，如果大家使用过程中有什么意见和建议，欢迎issue
  
  使用方法：
  
  1、添加权限，注册service
  
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    
    
    
     <service android:name="com.guaju.screenrecorderlibrary.RecordService" />
  
  2、在application中初始化实例
  
    如：
      private ScreenRecorderHelper instance;
      public static MyApplication app;
      @Override
      public void onCreate() {
          super.onCreate();
          app=this;
          //init
          instance = ScreenRecorderHelper.getInstance(this);
      }
      public ScreenRecorderHelper getSRHelper(){
          return instance;
      }
      得到ScreenRecorderHelper类
    
    并且别忘记在清单文件中配置 application  name
    
    
 
     
   3、在需要录屏的activity 或者fragment中初始化RecordService，如
   
      srHelper = MyApplication.getApp().getSRHelper();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isclick=true;
            srHelper.initRecordService(this);
        }
        由于我这个库只考虑到了5.0之后的，所以如果有5.0版本之前的手机需要录屏的话，请自行处理
        
   4、复写onActivityResult方法，我在screenRecorderHelper中也定义了一个onActivityResult方法，直接拿来使用即可，如
   
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("tag", "requestCode****" + requestCode);
        srHelper.onActivityResult(this, requestCode, resultCode, data, new ScreenRecorderHelper.OnRecordStatusChangeListener() {
            @Override
            public void onChangeSuccess() {
                //开始录制，处理开始录制后的事件
                dosomething
            }

            @Override
            public void onChangeFailed() {
                //如果录制失败，则不作任何变化
                 dosomething
            }
        });
    }
    
  5.准备工作就绪，直接操作开始录制按钮，和停止录制按钮即可
  
     srHelper.startRecord(MainActivity.this);
     
     srHelper.stopRecord(new ScreenRecorderHelper.OnRecordStatusChangeListener() {
                                            @Override
                                            public void onChangeSuccess() {
                                                //当停止成功，做界面变化
                                                //Toast.makeText(MainActivity.this, "录屏成功"+srHelper.getRecordFilePath(), Toast.LENGTH_SHORT).show();

                                            }

                                            @Override
                                            public void onChangeFailed() {
                                                //不作处理

                                            }
                                        });
  
   ok,走完上边五步就能够实现屏幕录制了，当然如果想改下代码的话，可以下载module库直接修改，多谢指教~~~ 
    
