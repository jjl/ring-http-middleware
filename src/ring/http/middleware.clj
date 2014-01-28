(ns ring.http.middleware)

(defn wrap-http-params [handler]
  (fn [request]
    (let [query-params (request :query-params)
          form-params (request :form-params)]
      (let [http-params (merge {} query-params form-params)]
        (handler (assoc request :http-params http-params))))))

(defn wrap-keyword-http-params [handler]
  (fn [request]
      (letfn [(kwify-1 [before]
                (reduce #(assoc %1 (keyword %2)
                                (get before %2)) {} (keys before)))
              (kwify [req kw]
                (let [item (req kw)]
                  (if item
                    (assoc req kw (kwify-1 item))
                    req)))]
        (handler (reduce kwify request '(:query-params :form-params))))))
