package com.suda.jzapp.ui.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar;

import android.support.design.widget.FloatingActionButton;

import com.suda.jzapp.BaseActivity;
import com.suda.jzapp.R;
import com.suda.jzapp.dao.bean.AccountDetailDO;
import com.suda.jzapp.dao.greendao.AccountType;
import com.suda.jzapp.manager.AccountManager;
import com.suda.jzapp.misc.Constant;
import com.suda.jzapp.misc.IntentConstant;
import com.suda.jzapp.util.IconTypeUtil;
import com.suda.jzapp.util.TextUtil;

import me.drakeet.materialdialog.MaterialDialog;

public class CreateOrEditAccountActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAccountID = getIntent().getLongExtra(IntentConstant.ACCOUNT_ID, 0l);
        accountManager = new AccountManager(this);

        initWidget();
    }

    @Override
    protected void initWidget() {
        mTvAccountName = (TextView) findViewById(R.id.account_name);
        mTvAccountTypeDesc = (TextView) findViewById(R.id.account_type_desc);
        mTvAccountRemark = (TextView) findViewById(R.id.account_remark);
        mIgAccountType = (ImageView) findViewById(R.id.account_type_icon);
        mTvAccountMoney = (TextView) findViewById(R.id.account_money);

        getSupportActionBar().setTitle(mAccountID == 0 ? "创建账户" : "修改账户");

        if (mAccountID > 0) {
            //获取展示账户
            AccountDetailDO account = accountManager.getAccountByID(mAccountID);

            accountName = account.getAccountName();
            accountRemark = account.getAccountRemark();
            mTvAccountName.setText(accountName);
            accountTypeId = account.getAccountTypeID();
            mTvAccountTypeDesc.setText(account.getAccountDesc());
            mIgAccountType.setImageResource(IconTypeUtil.getAccountIcon(account.getAccountIcon()));
            mTvAccountRemark.setText(accountRemark);
            accountMoney = account.getAccountMoney();
            mTvAccountMoney.setText(String.format(getResources().getString(R.string.money_format), accountMoney));

        }

        mSubmitButton = (FloatingActionButton) findViewById(R.id.submit);
        mSubmitButton.setImageResource(mAccountID > 0 ? R.drawable.ic_delete_white : R.drawable.ic_done_white);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountID > 0) {
                    final MaterialDialog materialDialog = new MaterialDialog(CreateOrEditAccountActivity.this);
                    materialDialog.setTitle("删除账户？")
                            .setMessage("")
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    accountManager.deleteAccountByID(mAccountID, null);
                                    finish();
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                }
                            }).show();
                } else {
                    if (TextUtils.isEmpty(accountName)) {

                        Snackbar.make(v, "请填写账户名称", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .show();
//
//                        new SnackBar.Builder(CreateOrEditAccountActivity.this)
//                                .withMessage("请填写账户名称")
//                                .withDuration(SnackBar.SHORT_SNACK)
//                                .withBackgroundColorId(getMainTheme().getMainColorID())
//                                .show();
                        return;
                    }
                    accountManager.createNewAccount(accountName, accountMoney, accountTypeId,
                            accountRemark, null);
                    finish();
                }


            }
        });


    }


    public void editAccount(View view) {
        String tag = view.getTag().toString();
        Intent intent = new Intent(this, EditAccountActivity.class);

        intent.putExtra(IntentConstant.ACCOUNT_ID, mAccountID);

        if ("editAccountName".equals(tag)) {
            intent.putExtra(IntentConstant.EDIT_TYPE, EditAccountActivity.PROP_TYPE_ACCOUNT_NAME);
            intent.putExtra(IntentConstant.EDIT_ACCOUNT_NAME, accountName);
            startActivityForResult(intent, EditAccountActivity.PROP_TYPE_ACCOUNT_NAME);
        } else if ("editAccountMoney".equals(tag)) {
            intent.putExtra(IntentConstant.EDIT_TYPE, EditAccountActivity.PROP_TYPE_ACCOUNT_MONEY);
            intent.putExtra(IntentConstant.EDIT_ACCOUNT_MONEY, accountMoney);
            startActivityForResult(intent, EditAccountActivity.PROP_TYPE_ACCOUNT_MONEY);
        } else if ("editAccountRemark".equals(tag)) {
            intent.putExtra(IntentConstant.EDIT_TYPE, EditAccountActivity.PROP_TYPE_ACCOUNT_REMARK);
            intent.putExtra(IntentConstant.EDIT_ACCOUNT_REMARK, accountRemark);
            startActivityForResult(intent, EditAccountActivity.PROP_TYPE_ACCOUNT_REMARK);
        } else if ("editAccountType".equals(tag)) {
            intent.putExtra(IntentConstant.EDIT_TYPE, EditAccountActivity.PROP_TYPE_ACCOUNT_TYPE);
            intent.putExtra(IntentConstant.EDIT_ACCOUNT_TYPE, accountTypeId);
            startActivityForResult(intent, EditAccountActivity.PROP_TYPE_ACCOUNT_TYPE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null)
            return;
        if (requestCode == EditAccountActivity.PROP_TYPE_ACCOUNT_NAME) {
            accountName = data.getStringExtra(IntentConstant.EDIT_ACCOUNT_NAME);
            mTvAccountName.setText(accountName);
        } else if (requestCode == EditAccountActivity.PROP_TYPE_ACCOUNT_MONEY) {
            accountMoney = data.getDoubleExtra(IntentConstant.EDIT_ACCOUNT_MONEY, 0);
            mTvAccountMoney.setText(String.format(getResources().getString(R.string.money_format), accountMoney));
        } else if (requestCode == EditAccountActivity.PROP_TYPE_ACCOUNT_REMARK) {
            accountRemark = data.getStringExtra(IntentConstant.EDIT_ACCOUNT_REMARK);
            mTvAccountRemark.setText(accountRemark);
        } else if (requestCode == EditAccountActivity.PROP_TYPE_ACCOUNT_TYPE) {
            accountTypeId = data.getIntExtra(IntentConstant.EDIT_ACCOUNT_TYPE, 0);
            accountManager.getAccountTypeByID(accountTypeId, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == Constant.MSG_ERROR)
                        return;

                    AccountType accountType = (AccountType) msg.obj;

                    mTvAccountTypeDesc.setText(accountType.getAccountDesc());
                    mIgAccountType.setImageResource(IconTypeUtil.getAccountIcon(accountType.getAccountIcon()));

                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSubmitButton.setBackgroundTintList(getResources().getColorStateList(getMainTheme().getMainColorID()));
        // mSubmitButton.setBackgroundColor(mainColor);
        // mSubmitButton.setColorNormal(mainColor);
        //  mSubmitButton.setColorPressed(mainDarkColor);
    }


    private FloatingActionButton mSubmitButton;
    private TextView mTvAccountName, mTvAccountTypeDesc, mTvAccountRemark, mTvAccountMoney;
    private ImageView mIgAccountType;

    private long mAccountID = 0;
    private double accountMoney = 0;
    private int accountTypeId = 1;
    private String accountName = "";
    private String accountRemark = "";
    private AccountManager accountManager;

}