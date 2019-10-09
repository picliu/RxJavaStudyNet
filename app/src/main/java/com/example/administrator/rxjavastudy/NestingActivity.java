package com.example.administrator.rxjavastudy;

import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        TextView mTvNestingForMap = findViewById(R.id.tv_nesting_for_map);
        TextView mTvNestingForFlatmap = findViewById(R.id.tv_nesting_for_flatmap);
        TextView mTvNestingForConcatMap = findViewById(R.id.tv_nesting_for_concatMap);
        mTvNestingNormal.setOnClickListener(this);
        mTvNesting.setOnClickListener(this);
        mTvNestingForMap.setOnClickListener(this);
        mTvNestingForFlatmap.setOnClickListener(this);
        mTvNestingForConcatMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_nesting_normal:
                initNormal();
                break;
            case R.id.tv_nesting:
                initflatMap();
                break;
            case R.id.tv_nesting_for_map:
                initMap();
                break;
            case R.id.tv_nesting_for_flatmap:
                initflat();
                break;
            case R.id.tv_nesting_for_concatMap:
                initconcatMap();
                break;
        }
    }

    private void initconcatMap() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
                emitter.onComplete();
            }
        }).concatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String s) throws Exception {

                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + s);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(Constans.TAG, "accept: s=" + s);
            }
        });
    }

    private void initflat() {
        /**
         * 发现 两次打印出来结果不一样，flatMap不保证事件顺序，如需保证顺序使用concatMap
         */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
//                emitter.onError(new Throwable("hahahah"));
                emitter.onNext("4");
                emitter.onNext("5");
                emitter.onComplete();

            }
        }).flatMap(new Function<String, ObservableSource<String>>() {

            @Override
            public ObservableSource<String> apply(final String s) throws Exception {

                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + s);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {

            @Override
            public void accept(String s) throws Exception {
                Log.i(Constans.TAG, "accept: s=" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i(Constans.TAG, "accept: throwable=" + throwable.toString());
            }
        });
    }

    private void initMap() {
        /**
         * 个人对map的理解
         * 上游发出的任何类型，经过map转换成其他类型，最后在下游返回
         */
        final List<String> arrayList = new ArrayList<>();
        arrayList.clear();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
                emitter.onNext("4");
            }
        }).map(new Function<String, List<String>>() {
            @Override
            public List<String> apply(String s) throws Exception {
                arrayList.add(s);
                return arrayList;
            }
        }).subscribe(new Consumer<List<String>>() {
            @Override
            public void accept(List<String> list) throws Exception {
                for (String s : list) {
                    Log.i(Constans.TAG, "accept: s=" + s);
                }

            }
        });
    }

    private void initflatMap() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {

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
