ring-http-middleware
====================

Middleware for more convenience dealing with http params

Installation
--------------

Add the following dependency to your project.clj file:

```[ring-http-middleware "0.1.1"]```

What does it really do?
----------------------------

There are two middleware wrappers, wrap-http-params and wrap-keyword-http-params

wrap-http-params provides :http-params, a merged map of :query-params and :form-params (because in compojure, the :params key also contains route params)

wrap-keyword-http-params keywordifies :query-params and :form-params

To use them together, you'll want to use wrap-keyword-http-params first, otherwise :http-params will not be keywordified
