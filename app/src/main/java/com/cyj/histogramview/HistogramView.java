package com.cyj.histogramview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.List;

/***
 * 自定义柱状图
 */
public class HistogramView extends View {
    private Paint mAexsPaint;// 坐标轴 轴线 画笔：
    private Paint mAexsTextPaint;// 绘制坐标轴文本的画笔
    private Paint mTopTextPaint;// 绘制坐标轴文本的画笔
    private Paint mHistogramPaint;// 矩形画笔 柱状图的样式信息

    private List<Integer> aniProgress = new ArrayList<>();// 实现动画的值
    private HistogramAnimation ani;

    private ArrayList<String> nameLists = new ArrayList<>();
    private ArrayList<String> countLists = new ArrayList<>();
    float histogramItemWidth = dp2px(20);//柱状图宽度
    int columWidth;//每列宽度
    int columCount = 1;//总列数
    int maxValue = 0;//最大值

    int axesColor;//坐标轴线颜色
    int axesTextColor;//坐标轴文字颜色
    float axesTextSize;//坐标轴文字大小
    int topTextColor;//顶部文字颜色
    float topTextSize;//顶部文字大小

    int mWidth;
    int mHeight;
    float histogramHeight;//柱状图高度
    float mMarginTop;//上边界

    float bottomAxesTextHeight = dp2px(50);

    public HistogramView(Context context) {
        super(context);
        init();
    }

    public HistogramView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HistogramView);
        histogramItemWidth = typedArray.getDimension(R.styleable.HistogramView_axesTextSize, dp2px(20));
        axesColor = typedArray.getColor(R.styleable.HistogramView_axesColor, getResources().getColor(R.color.accentColor));
        axesTextColor = typedArray.getColor(R.styleable.HistogramView_axesTextColor, getResources().getColor(R.color.accentColor));
        axesTextSize = typedArray.getDimension(R.styleable.HistogramView_axesTextSize, sp2px(12));
        topTextColor = typedArray.getColor(R.styleable.HistogramView_topTextColor, getResources().getColor(R.color.accentColor));
        topTextSize = typedArray.getDimension(R.styleable.HistogramView_topTextSize, sp2px(12));
        typedArray.recycle();

        init();
    }

    float mMargin;

    private void init() {
        ani = new HistogramAnimation();
        ani.setDuration(2000);
        mMargin = dp2px(20);

        //初始化坐标轴画笔
        mAexsPaint = new Paint();
        mAexsPaint.setColor(axesColor);
        mAexsPaint.setAntiAlias(true);
        //坐标轴文字画笔
        mAexsTextPaint = new TextPaint();
        mAexsTextPaint.setStyle(Paint.Style.FILL);
        mAexsTextPaint.setColor(axesTextColor);
        mAexsTextPaint.setTextSize(axesTextSize);
        mAexsTextPaint.setAntiAlias(true);
        mAexsTextPaint.setTextAlign(Paint.Align.LEFT);
        //顶部文字画笔
        mTopTextPaint = new TextPaint();
        mTopTextPaint.setStyle(Paint.Style.FILL);
        mTopTextPaint.setColor(topTextColor);
        mTopTextPaint.setTextSize(topTextSize);
        mTopTextPaint.setAntiAlias(true);
        mTopTextPaint.setTextAlign(Paint.Align.LEFT);
        //柱状图画笔
        mHistogramPaint = new Paint();
        mHistogramPaint.setAntiAlias(true);// 抗锯齿效果
        mHistogramPaint.setStyle(Paint.Style.FILL);
        mHistogramPaint.setColor(Color.parseColor("#9e95e9f3"));
    }

    public void start(ArrayList data, ArrayList countList) {
        this.nameLists = data;
        this.countLists = countList;
        columCount = nameLists.size();
        for (int i = 0; i < columCount; i++) {
            int value = Integer.parseInt(countLists.get(i));
            if (value > maxValue) {
                maxValue = value;
            }
            aniProgress.add(0);
        }
        this.startAnimation(ani);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getHeight();
        histogramHeight = mHeight - bottomAxesTextHeight - getPaddingTop();
        mMarginTop = getPaddingTop();

        columWidth = (int) ((mWidth - mMargin) / columCount);//项的宽度

        drawAxesLineAndAxes(canvas);//绘制坐标线及y轴刻度

        drawAxesText(canvas);//绘制x轴值

        drawHistogramItem(canvas);//绘制柱状图及柱状图上文字

    }

    private void drawHistogramItem(Canvas canvas) {
        // 绘制矩形
        if (aniProgress != null && aniProgress.size() > 0) {
            for (int i = 0; i < aniProgress.size(); i++) {// 循环遍历将柱状图形画出来
                int value = aniProgress.get(i);

                float left = (columWidth - histogramItemWidth) / 2 + i * columWidth + mMargin;
                float right = left + histogramItemWidth;
                float t = (maxValue) * 1000;
                float top = histogramHeight - histogramHeight * (value / t) + mMarginTop;
                float bottom = histogramHeight + mMarginTop;

                //进行paint设颜色
                if (i % 4 == 0) {
                    mHistogramPaint.setColor(getResources().getColor(R.color.subColor));
                    mTopTextPaint.setColor(getResources().getColor(R.color.subColor));
                } else if (i % 4 == 1) {
                    mHistogramPaint.setColor(getResources().getColor(R.color.subColor1));
                    mTopTextPaint.setColor(getResources().getColor(R.color.subColor1));
                } else if (i % 4 == 2) {
                    mHistogramPaint.setColor(getResources().getColor(R.color.subColor2));
                    mTopTextPaint.setColor(getResources().getColor(R.color.subColor2));
                } else if (i % 4 == 3) {
                    mHistogramPaint.setColor(getResources().getColor(R.color.subColor3));
                    mTopTextPaint.setColor(getResources().getColor(R.color.subColor3));
                }
                RectF rect1 = new RectF(left, top, right, bottom);// 柱状图的形状

                canvas.drawRoundRect(rect1, (histogramItemWidth) / 2, (histogramItemWidth) / 2, mHistogramPaint);

                //设置柱形上面的文字
                float textWidth = mTopTextPaint.measureText(countLists.get(i));
                if (textWidth > columWidth) {
                    textWidth = 0;
                } else {
                    textWidth = (columWidth - textWidth) / 2;
                }
                canvas.drawText(countLists.get(i) + "", (i) * columWidth + textWidth + mMargin, top - dp2px(10), mTopTextPaint);
            }
        }
    }

    private void drawAxesText(Canvas canvas) {
        for (int i = 0; i < columCount; i++) {
            // 设置底部的文字
            float textWidth = mAexsTextPaint.measureText(nameLists.get(i));
            if (textWidth > columWidth) {
                textWidth = columWidth / 2;
            } else {
                textWidth = (columWidth - textWidth) / 2;
            }
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(axesTextColor);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(axesTextSize);
            StaticLayout layout = new StaticLayout(nameLists.get(i), textPaint, columWidth,
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            canvas.save();
            canvas.translate((i) * columWidth + textWidth + mMargin, mHeight - bottomAxesTextHeight + mMargin / 2);
            layout.draw(canvas);
            canvas.restore();//别忘了restore

        }

    }

    private void drawAxesLineAndAxes(Canvas canvas) {
        // 绘制底部的线条
        canvas.drawLine(0, mHeight - bottomAxesTextHeight, mWidth, mHeight - bottomAxesTextHeight, mAexsPaint);
        // 绘制底部的线条
        canvas.drawLine(mMargin, mMarginTop, mMargin, mHeight - bottomAxesTextHeight + mMargin, mAexsPaint);

        canvas.drawText("0", mMargin / 2, mHeight - bottomAxesTextHeight, mAexsTextPaint);
        canvas.drawText(maxValue + "", mMargin / 2, mHeight - bottomAxesTextHeight - histogramHeight, mAexsTextPaint);
    }

    private int dp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().density;
        return (int) (v * value + 0.5f);
    }

    private int sp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (v * value + 0.5f);
    }


    /**
     * 集成animation的一个动画类
     */
    private class HistogramAnimation extends Animation {
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            for (int i = 0; i < aniProgress.size(); i++) {
                aniProgress.set(i, (int) (Integer.parseInt(countLists.get(i)) * 1000 * interpolatedTime));
            }
            invalidate();
        }
    }

}
