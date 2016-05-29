FROM sequenceiq/hadoop-docker

RUN rm /usr/local/hadoop/etc/hadoop/mapred-site.xml
RUN rm /etc/bootstrap.sh
RUN rm /usr/local/hadoop/etc/hadoop/slaves


COPY ./masters /usr/local/hadoop/etc/hadoop/
COPY ./slaves /usr/local/hadoop/etc/hadoop/
COPY ./mapred-site.xml /usr/local/hadoop/etc/hadoop/
COPY ./bootstrap.sh /etc/
