;; project settings
(setq ent-project-home (file-name-directory (if load-file-name load-file-name buffer-file-name)))
(setq ent-project-name "api-duckling")
(setq ent-clean-regexp "~$\\|\\.tex$")
(setq ent-project-config-filename "APIDuckling.org")

;; local functions

(defvar project-version)

(setq project-version (ent-get-version))

(defun make-image-tag (&optional version)
   "Make docker image tag using ent variables."
   (concat "dpom/" ent-project-name ":" (or version project-version)))

;; tasks

(load ent-init-file)

;; (task 'org2md '() "convert org doc to md" '(lambda (&optional x) "cd docs; make all; cd .."))

;; (task 'api '() "build the API documentation" '(lambda (&optional x) "lein codox"))

;; (task 'doc '(org2md api) "build the project documentation" '(lambda (&optional x) "ls docs"))

(task 'format '() "format the project" '(lambda (&optional x) "lein cljfmt fix"))

(task 'check '() "check the project" '(lambda (&optional x) "lein checkall"))

(task 'tree '() "tree dependencies" '(lambda (&optional x) "lein do clean, deps :tree"))

(task 'tests '() "run tests" '(lambda (&optional x) "lein do clean, test"))

(task 'libupdate () "update project libraries" '(lambda (&optional x) "lein ancient :no-colors"))

(task 'package '() "package the library" '(lambda (&optional x) "lein do clean, uberjar"))

(task 'run '() "run the server" '(lambda (&optional x) "lein ring server-headless"))

;; (task 'deploy '() "deploy to clojars" '(lambda (&optional x) "lein deploy clojars"))

(task 'deps '() "load libs" '(lambda (&optional x) "lein deps"))

;; (task 'heroku '(deps) "deploy to heroku" '(lambda (&optional x) "heroku container:push web"))

(task 'dockerbuild '(deps) "build docker image" '(lambda (&optional x) (concat "docker"
                                                                               " build"
                                                                               " -t " (make-image-tag)
                                                                               " -t " (make-image-tag "latest")
                                                                               " .")))

(task 'dockerpush '(dockerbuild) "push image to docker hub" '(lambda (&optional x) (concat "docker push " (make-image-tag)
                                                                                           ";docker push " (make-image-tag "latest"))))


;; Local Variables:
;; no-byte-compile: t
;; no-update-autoloads: t
;; End:
