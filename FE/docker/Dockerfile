FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf

RUN mkdir -p /etc/letsencrypt
EXPOSE 5000

CMD ["nginx", "-g", "daemon off;"]