FROM sequenceiq/hadoop-docker


RUN rm /usr/local/hadoop/etc/hadoop/mapred-site.xml
RUN rm /usr/local/hadoop/etc/hadoop/hdfs-site.xml
RUN rm /etc/bootstrap.sh


COPY ./mapred-site.xml /usr/local/hadoop/etc/hadoop/
COPY ./hdfs-site.xml /usr/local/hadoop/etc/hadoop/
COPY ./bootstrap.sh /etc/

WORKDIR /usr/local/hadoop/