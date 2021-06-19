FROM gitpod/workspace-full-vnc:branch-pr-349

USER root
RUN dpkg --add-architecture i386 \
  && apt-get update \
  && apt-get install -y --no-install-recommends -o APT::Immediate-Configure=0 \
    cabextract=1.* \
    libxext6=2* \
    libxext6:i386 \
    libfreetype6=2.10* \
    libfreetype6:i386=2.10* \
    fakeroot=1.24* \
&& apt-get clean && rm -rf /var/cache/apt/* && rm -rf /var/lib/apt/lists/* && rm -rf /tmp/*

USER gitpod
# activate java 11. It's already installed in the base image.
RUN  bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java"
