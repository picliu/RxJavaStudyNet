package com.example.administrator.rxjavastudy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * *  @name:picliu
 * *  @date: 2019-09-29
 */
public class BaseUseActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "BaseUseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_use_activity);

        initView();
    }

    private void initView() {
        TextView mTvBaseUse = findViewById(R.id.tv_base_use);
        TextView mTvBaseUseByChain = findViewById(R.id.tv_base_use_by_chain);
        TextView mTvBaseUseByThread = findViewById(R.id.tv_base_use_by_thread);
        mTvBaseUse.setOnClickListener(this);
        mTvBaseUseByChain.setOnClickListener(this);
        mTvBaseUseByThread.setOnClickListener(this);
    }

    private void initCreat1() {
//        创建被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                默认 被观察者和观察者在同一线程 （主线程）
//                Log.i("kishenone", "emitter 1" + Thread.currentThread().getName());
                emitter.onNext(1);
//                Log.i("kishenone", "emitter 2" + Thread.currentThread().getName());
//                emitter.onNext(2);
                Log.i("kishenone", "Observable thread is :" + Thread.currentThread().getName());
                emitter.onComplete();
            }
        })
                /*被观察者 设置两次线程 仅第一次生效*/
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                /*
        1. 被观察者 线程放在新的线程时  观察者不设置   观察者也在新的线程
        2. 被观察者放在新的线程 观察者放在新线程    观察者即在新的线程
        3.被观察者放在新线程  观察者放在主线程   观察者即在主线程*/
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i("kishenone", "Consumer 1 thread is :" + Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
                ./*被观察者与观察者建立连接*/subscribe(/*  创建观察者  */new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                Log.i("kishenone", "Consumer 2 thread is :" + Thread.currentThread().getName());
            }
        });
    }

    /**
     * 最最基础的 creat  创建被观察者
     */
    private void initCreat() {
//创建被观察者
        Observable<Integer> integerObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                被观察者向观察者发送事件
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        });

//创建观察者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                Log.i(TAG, "onNext: value=" + value);
//                接受上面发送的消息
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };
//        产生订阅关系
        integerObservable.subscribe(observer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_base_use:
                initCreat();
                break;
            case R.id.tv_base_use_by_thread:
                initCreat1();
                break;
            case R.id.tv_base_use_by_chain:
                initCreatByChain();
                break;
        }
    }

    private void initCreatByChain() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onComplete();
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "initCreatByChain  onSubscribe: thread=" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(String value) {
                Log.i(TAG, "initCreatByChain onNext: value=" + value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.i(TAG, "initCreatByChain onComplete:");
            }
        });
    }
}
