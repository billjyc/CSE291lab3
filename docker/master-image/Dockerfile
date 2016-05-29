FROM sequenceiq/hadoop-docker

RUN rm /usr/local/hadoop/etc/hadoop/mapred-site.xml
RUN rm /usr/local/hadoop/etc/hadoop/hdfs-site.xml
RUN rm /etc/bootstrap.sh
RUN rm /usr/local/hadoop/etc/hadoop/slaves
ENV HADOOP_CLASSPATH /usr/java/default/lib/tools.jar


COPY ./masters /usr/local/hadoop/etc/hadoop/
COPY ./slaves /usr/local/hadoop/etc/hadoop/
COPY ./mapred-site.xml /usr/local/hadoop/etc/hadoop/
COPY ./hdfs-site.xml /usr/local/hadoop/etc/hadoop/
COPY ./bootstrap.sh /etc/
COPY ./WordCount.java /usr/local/hadoop/
COPY ./Combiner.java /usr/local/hadoop/
COPY ./cnn_news.txt /usr/local/hadoop/

WORKDIR /usr/local/hadoop/
