install: undeploy deploy
.PHONY: deploy
deploy:
	helm install --name ${artifactId} ${artifactId}/
.PHONY: undeploy
undeploy:
	if helm list | grep -q ${artifactId}; then helm delete --purge ${artifactId}; fi
