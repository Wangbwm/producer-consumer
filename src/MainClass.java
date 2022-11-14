import java.util.LinkedList;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class MainClass {
    public static void main(String[] args) {
        Scanner reader=new Scanner(System.in);
        public_value value=new public_value();
        product function1=new product(value);
        customer function2=new customer(value);
        System.out.println("输入生产者线程个数：");
        Integer product_num=reader.nextInt();
        System.out.println("输入消费者线程个数：");
        Integer customer_num=reader.nextInt();
        if(product_num>customer_num){
            for(int i=1;i<=product_num;++i){
                Thread childThread=new Thread(function1,"生产者线程"+i);
                childThread.start();
                if(i<=customer_num){
                    Thread childThread2=new Thread(function2,"消费者线程"+i);
                    childThread2.start();
                }


            }
        }else {
            for(int i=1;i<=customer_num;++i){
                if(i<=product_num){
                    Thread childThread2=new Thread(function1,"生产者线程"+i);
                    childThread2.start();
                }
                Thread childThread=new Thread(function2,"消费者线程"+i);
                childThread.start();



            }
        }

    }
}

class product implements Runnable{
    public_value value;
    Integer sum=0;
    product(public_value value){
        this.value=value;
    }
    @Override
    public void run() {
        while(true){
            synchronized (value.lock) {
                if (value.Warehouse.size() >= 10) {
                    System.out.println(Thread.currentThread().getName()+"：仓库积压，停止生产");
                    ++sum;
                    if(sum>10){
                        System.out.println(Thread.currentThread().getName()+"：生产者退出市场");
                        break;
                    }
                    value.lock.notify();
                    try {
                        sleep(1000);
                        value.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    value.Warehouse.add("产品" + value.num);
                    System.out.println(Thread.currentThread().getName() + ":生产者放入 产品" + value.num);
                    ++value.num;
                    value.lock.notify();
                    try {
                        sleep(1000);
                        value.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}

class customer implements Runnable{
    public_value value;
    Integer sum=0;
    customer(public_value value){
        this.value=value;
    }
    @Override
    public void run() {
        while(true){
            synchronized (value.lock){
                if(value.Warehouse.isEmpty()){
                    System.out.println(Thread.currentThread().getName()+"：货源紧缺，顾客等待");
                    ++sum;
                    if(sum>10){
                        System.out.println(Thread.currentThread().getName()+"：消费者失去耐心，不再等待");
                    }
                    value.lock.notify();
                    try {
                        sleep(1000);
                        value.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    String name=value.Warehouse.pollFirst();
                    System.out.println(Thread.currentThread().getName()+":消费者取走 "+name);
                    value.lock.notify();
                    try {
                        sleep(1000);
                        value.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}

class public_value{
    Object lock=new Object();
    LinkedList<String> Warehouse=new LinkedList<>();
    Integer num=1;

}