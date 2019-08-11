package com.example.administrator.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initCreat();
        initTest();
    }

    private void initTest() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d("kishenone", "Observable thread name=" + Thread.currentThread().getName());
                emitter.onNext(1);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d("kishenone", "Observer thread name=" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void initCreat() {
////        创建被观察者
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
////                默认 被观察者和观察者在同一线程 （主线程）
////                Log.i("kishenone", "emitter 1" + Thread.currentThread().getName());
//                emitter.onNext(1);
////                Log.i("kishenone", "emitter 2" + Thread.currentThread().getName());
////                emitter.onNext(2);
//                Log.i("kishenone", "Observable thread is :" + Thread.currentThread().getName());
//                emitter.onComplete();
//            }
//        })
//                /*被观察者 设置两次线程 仅第一次生效*/
//                .subscribeOn(Schedulers.newThread())
//                .subscribeOn(AndroidSchedulers.mainThread())
//                /*
//        1. 被观察者 线程放在新的线程时  观察者不设置   观察者也在新的线程
//        2. 被观察者放在新的线程 观察者放在新线程    观察者即在新的线程
//        3.被观察者放在新线程  观察者放在主线程   观察者即在主线程*/
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        Log.i("kishenone", "Consumer 1 thread is :" + Thread.currentThread().getName());
//                    }
//                })
//                .observeOn(Schedulers.io())
////                .observeOn(Schedulers.io())
//                ./*被观察者与观察者建立连接*/subscribe(/*  创建观察者  */new Consumer<Integer>() {
//
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.i("kishenone", "Consumer 2 thread is :" + Thread.currentThread().getName());
//            }
//        });


    }
}
