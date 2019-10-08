package com.example.administrator.rxjavastudy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * *  @name:picliu
 * *  @date: 2019-09-30
 */
public class NestingActivity extends AppCompatActivity implements View.OnClickListener {
    private int loginCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nesting);
        initView();
    }

    private void initView() {
        TextView mTvNestingNormal = findViewById(R.id.tv_nesting_normal);
        TextView mTvNesting = findViewById(R.id.tv_nesting);
        mTvNestingNormal.setOnClickListener(this);
        mTvNesting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_nesting_normal:
                initNormal();
                break;
            case R.id.tv_nesting:
                initMap();
                break;
        }
    }

    private void initMap() {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                loginCount++;
                if (loginCount % 2 == 0) {
                    emitter.onNext(true);
                } else {
                    emitter.onError(new Throwable("注册失败"));
                }
                emitter.onComplete();
                Log.i(Constans.TAG, " 注册 subscribe: Thread=" + Thread.currentThread().getName());
            }
        }).observeOn(Schedulers.io())
                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {

                    @Override
                    public ObservableSource<Boolean> apply(final Boolean aBoolean) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                                if (aBoolean) {
                                    emitter.onNext(true);
                                } else {
                                    emitter.onError(new Throwable("失败"));
                                }
                                Log.i(Constans.TAG, " 注册结束  开始登录 accept: Thread=" + Thread.currentThread().getName());
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Toast.makeText(NestingActivity.this, "成功，进入主页", Toast.LENGTH_SHORT).show();
                        Log.i(Constans.TAG, " 注册结束 登录结果 成功: Thread=" + Thread.currentThread().getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(NestingActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i(Constans.TAG, "  失败: Thread=" + Thread.currentThread().getName() + "  throwable=" + throwable.toString());
                    }
                });
    }

    private void initNormal() {
        registr();
    }

    private void registr() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                Log.i(Constans.TAG, "ThreadName=" + Thread.currentThread().getName());
                loginCount++;
                if (loginCount % 2 == 0) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }

                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.i(Constans.TAG, "registr  ThreadName=" + Thread.currentThread().getName());
                        if (aBoolean) {
                            Toast.makeText(NestingActivity.this, "成功,进入登录", Toast.LENGTH_SHORT).show();
                            login();
                        } else {
                            Toast.makeText(NestingActivity.this, "失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void login() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                Log.i(Constans.TAG, "login  ThreadName=" + Thread.currentThread().getName());
                if (loginCount % 2 == 0) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }

                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Toast.makeText(NestingActivity.this, "成功,进入主页", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NestingActivity.this, "失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
