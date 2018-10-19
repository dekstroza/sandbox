install: undeploy deploy
.PHONY: deploy
deploy:
	helm install --name thorntail4-app --namespace techday-2018 thorntail4-app/
.PHONY: undeploy
undeploy:
	if helm list | grep -q thorntail4-app; then helm delete --purge thorntail4-app; fi
