package com.github.tvbox.osc.ui.fragment;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.ui.activity.SettingActivity;
import com.github.tvbox.osc.ui.dialog.AboutDialog;
import com.github.tvbox.osc.ui.dialog.ChangeIJKCodeDialog;
import com.github.tvbox.osc.ui.dialog.ChangePlayDialog;
import com.github.tvbox.osc.ui.dialog.ChangeRenderDialog;
import com.github.tvbox.osc.ui.dialog.XWalkInitDialog;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.XWalkUtils;
import com.orhanobut.hawk.Hawk;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class ModelSettingFragment extends BaseLazyFragment {
    private TextView tvDebugOpen;
    private TextView tvTestChannel;
    private TextView tvMediaCodec;
    private TextView tvSourceMode;
    private TextView tvParseWebView;
    private TextView tvPlay;
    private TextView tvRender;
    private TextView tvXWalkDown;

    public static ModelSettingFragment newInstance() {
        return new ModelSettingFragment().setArguments();
    }

    public ModelSettingFragment setArguments() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_model;
    }

    @Override
    protected void init() {
        tvDebugOpen = findViewById(R.id.tvDebugOpen);
        tvTestChannel = findViewById(R.id.tvTestChannel);
        tvSourceMode = findViewById(R.id.tvSourceMode);
        tvParseWebView = findViewById(R.id.tvParseWebView);
        tvMediaCodec = findViewById(R.id.tvMediaCodec);
        tvPlay = findViewById(R.id.tvPlay);
        tvRender = findViewById(R.id.tvRenderType);
        tvXWalkDown = findViewById(R.id.tvXWalkDown);
        tvMediaCodec.setText(Hawk.get(HawkConfig.IJK_CODEC, ""));
        tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "?????????" : "?????????");
        tvSourceMode.setText(Hawk.get(HawkConfig.SOURCE_MODE_LOCAL, true) ? "??????" : "??????");
        tvTestChannel.setText(Hawk.get(HawkConfig.TEST_CHANNEL, false) ? "?????????" : "?????????");
        tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "????????????" : "XWalkView");
        tvXWalkDown.setText(XWalkUtils.xWalkLibExist(mContext) ? "?????????" : "?????????");
        findViewById(R.id.llXWalkCore).setVisibility(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? View.GONE : View.VISIBLE);
        changePlay();
        changeRender();
        findViewById(R.id.llDebug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.DEBUG_OPEN, !Hawk.get(HawkConfig.DEBUG_OPEN, false));
                tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "?????????" : "?????????");
            }
        });
        findViewById(R.id.llTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.TEST_CHANNEL, !Hawk.get(HawkConfig.TEST_CHANNEL, false));
                tvTestChannel.setText(Hawk.get(HawkConfig.TEST_CHANNEL, false) ? "?????????" : "?????????");
            }
        });
        findViewById(R.id.llSourceMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.SOURCE_MODE_LOCAL, !Hawk.get(HawkConfig.SOURCE_MODE_LOCAL, true));
                tvSourceMode.setText(Hawk.get(HawkConfig.SOURCE_MODE_LOCAL, true) ? "??????" : "??????");
            }
        });
        findViewById(R.id.llParseWebVew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                boolean useSystem = !Hawk.get(HawkConfig.PARSE_WEBVIEW, true);
                Hawk.put(HawkConfig.PARSE_WEBVIEW, useSystem);
                tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "????????????" : "XWalkView");
                if (!useSystem) {
                    Toast.makeText(mContext, "??????: XWalkView?????????????????????Android?????????Android4.4??????????????????????????????", Toast.LENGTH_LONG).show();
                    if (!XWalkUtils.xWalkLibExist(mContext)) {
                        XWalkInitDialog dialog = new XWalkInitDialog().setOnListener(new XWalkInitDialog.OnListener() {
                            @Override
                            public void onchange() {
                                tvXWalkDown.setText(XWalkUtils.xWalkLibExist(mContext) ? "?????????" : "?????????");
                            }
                        }).build(mContext);
                        dialog.show();
                    }
                }
                findViewById(R.id.llXWalkCore).setVisibility(useSystem ? View.GONE : View.VISIBLE);
            }
        });
        findViewById(R.id.llXWalkCore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                XWalkInitDialog dialog = new XWalkInitDialog().setOnListener(new XWalkInitDialog.OnListener() {
                    @Override
                    public void onchange() {
                        tvXWalkDown.setText(XWalkUtils.xWalkLibExist(mContext) ? "?????????" : "?????????");
                    }
                }).build(mContext);
                dialog.show();
            }
        });
        findViewById(R.id.llAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                AboutDialog dialog = new AboutDialog().build(mActivity);
                dialog.show();
            }
        });
        findViewById(R.id.llMediaCodec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                ChangeIJKCodeDialog dialog = new ChangeIJKCodeDialog().build(mActivity, new ChangeIJKCodeDialog.Callback() {
                    @Override
                    public void change() {
                        tvMediaCodec.setText(Hawk.get(HawkConfig.IJK_CODEC, ""));
                    }
                });
                dialog.show();
            }
        });
        findViewById(R.id.llPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                new ChangePlayDialog().setOnChangePlayListener(new ChangePlayDialog.OnChangePlayListener() {
                    @Override
                    public void onChange() {
                        changePlay();
                        PlayerHelper.init();
                    }
                }).build(mContext).show();
            }
        });
        findViewById(R.id.llRender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                new ChangeRenderDialog().setOnChangePlayListener(new ChangeRenderDialog.OnChangeRenderListener() {
                    @Override
                    public void onChange() {
                        changeRender();
                        PlayerHelper.init();
                    }
                }).build(mContext).show();
            }
        });
        SettingActivity.callback = new SettingActivity.DevModeCallback() {
            @Override
            public void onChange() {
                findViewById(R.id.llDebug).setVisibility(View.VISIBLE);
                findViewById(R.id.llTest).setVisibility(View.VISIBLE);
                findViewById(R.id.llSourceMode).setVisibility(View.VISIBLE);
            }
        };
    }

    private void changePlay() {
        int playType = Hawk.get(HawkConfig.PLAY_TYPE, 0);
        if (playType == 1) {
            tvPlay.setText("IJK?????????");
        } else if (playType == 2) {
            tvPlay.setText("Exo?????????");
        } else {
            tvPlay.setText("???????????????");
        }
    }

    private void changeRender() {
        int renderType = Hawk.get(HawkConfig.PLAY_RENDER, 0);
        if (renderType == 0) {
            tvRender.setText("TextureView");
        } else if (renderType == 1) {
            tvRender.setText("SurfaceView");
        } else {
            tvRender.setText("TextureView");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SettingActivity.callback = null;
    }
}