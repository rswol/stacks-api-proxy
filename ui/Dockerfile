FROM node:16-bullseye

RUN apt update && apt upgrade -y && apt install git -y
RUN git clone https://github.com/rswol/stacks-proxy-frontend.git
WORKDIR /stacks-proxy-frontend
RUN npm install
RUN npm install -g serve
RUN REACT_APP_BACKEND=http://localhost:3000/ npm run build
EXPOSE 3000
CMD ["serve", "-s", "build"]
