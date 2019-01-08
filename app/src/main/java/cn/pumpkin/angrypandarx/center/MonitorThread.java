package cn.pumpkin.angrypandarx.center;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/7 11:25
 * @des:
 * @see {@link }
 */

public class MonitorThread extends Thread {

    @Override
    public void run() {
        super.run();

        while (true){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
