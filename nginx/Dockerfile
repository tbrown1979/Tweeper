# FROM ubuntu:14.04
# RUN apt-get update && apt-get install -y nginx
# RUN ln -sf /dev/stdout /var/log/nginx/access.log
# RUN ln -sf /dev/stderr /var/log/nginx/error.log
# RUN rm -rf /etc/nginx/sites-enabled/default
# EXPOSE 80 443
# COPY conf/mysite.com /etc/nginx/sites-enabled/mysite.com
# CMD ["nginx", "-g", "daemon off;"]


FROM ubuntu:14.04

# Largely from: https://github.com/dockerfile/nginx/blob/master/Dockerfile
RUN \
  # add-apt-repository -y ppa:nginx/stable && \
  apt-get update && \
  apt-get install -y nginx && \ 
  rm -rf /var/lib/apt/lists/* && \
  echo "\ndaemon off;" >> /etc/nginx/nginx.conf && \
  chown -R www-data:www-data /var/lib/nginx

WORKDIR /etc/nginx

VOLUME /var/log/nginx
VOLUME /usr/share/nginx/html/images/

EXPOSE 80
EXPOSE 443

RUN rm -f /etc/nginx/sites-enabled/default

ADD myapp.conf /etc/nginx/sites-enabled/myapp.conf
ADD nginx.conf /etc/nginx/nginx.conf

CMD ["nginx"]
