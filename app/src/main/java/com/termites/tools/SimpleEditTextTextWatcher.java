package com.termites.tools;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本输入内容监听器TextWatcher的简单封装
 * Created by LF on 16/10/20.
 */
public class SimpleEditTextTextWatcher implements TextWatcher {
	private EditText Et;
	private ImageView Img;
	private boolean IsZeroMatches = false;// 是否添加输入框开头不能输入0的规则
	private boolean IsChineseMatches = false;// 是否添加输入框不能输入汉字的规则
	private boolean IsMatchesTwoPoints = false;// 限制输入框是否能输入小数点后两位

	public SimpleEditTextTextWatcher(EditText et, ImageView img, boolean isZeroMatches, boolean isChineseMatches) {
		this.Et = et;
		this.Img = img;
		this.IsZeroMatches = isZeroMatches;
		this.IsChineseMatches = isChineseMatches;
	}

	public SimpleEditTextTextWatcher(EditText et, ImageView img, boolean isZeroMatches, boolean isChineseMatches, boolean isMatchesTwoPoints) {
		this.Et = et;
		this.Img = img;
		this.IsZeroMatches = isZeroMatches;
		this.IsChineseMatches = isChineseMatches;
		this.IsMatchesTwoPoints = isMatchesTwoPoints;
	}

	/**
	 * 限制输入框是否能输入小数点后两位
	 */
	public void setMatchesPoints(CharSequence s, EditText Et) {
		if (!TextUtils.isEmpty(s)) {
			if (s.toString().contains(".")) {
				if (s.length() - 1 - s.toString().indexOf(".") > 2) {
					s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
					Et.setText(s);
					Et.setSelection(s.length());
				}
			}
			if (s.toString().trim().substring(0).equals(".")) {
				s = "0" + s;
				Et.setText(s);
				Et.setSelection(2);
			}

			if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
				if (!s.toString().substring(1, 2).equals(".")) {
					Et.setText(s.subSequence(0, 1));
					Et.setSelection(1);
					return;
				}
			}
		}

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		/**
		 * 清除按钮的显示或隐藏
		 */
		if (TextUtils.isEmpty(s.toString())) {
			Img.setVisibility(View.INVISIBLE);
		} else {
			Img.setVisibility(View.VISIBLE);
		}

		/**
		 * 限制输入框是否能输入小数点后两位
		 */
		if (IsMatchesTwoPoints) {
			setMatchesPoints(s, Et);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(Editable edt) {
		/**
		 * 是否添加输入框开头不能输入0的规则
		 */
		if (IsZeroMatches) {
			Pattern p = Pattern.compile("^0.*");
			Matcher m = p.matcher(edt.toString());
			if (m.matches()) {
				Et.setText("");
			}
		}
		/**
		 * 是否添加输入框不能输入汉字的规则
		 */
		if (IsChineseMatches) {
			String temp = edt.toString();
			if (edt.toString().getBytes().length != edt.length()) {
				edt.delete(temp.length() - 1, temp.length());
			}
		}

	}

}
