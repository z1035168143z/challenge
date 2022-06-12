package io.zzr.timer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zrzhao
 * @date 2022/6/10
 */
public class TimeReporter implements Reporter {

    @Override
    public void report() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("当前时间：" + sdf.format(new Date()));
    }

}
