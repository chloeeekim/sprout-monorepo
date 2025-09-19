if [ -n "$VERCEL_GIT_PREVIOUS_SHA" ]; then
  echo "Checking changes between $VERCEL_GIT_PREVIOUS_SHA and $VERCEL_GIT_COMMIT_SHA ..."
  CHANGED_FILES=$(git diff --name-only $VERCEL_GIT_PREVIOUS_SHA $VERCEL_GIT_COMMIT_SHA)

  if ! echo "$CHANGED_FILES" | grep -q "^apps/frontend/"; then
    echo "No changes in apps/frontend, skipping build."
    exit 0
  fi
fi

echo "Changes detected in apps/frontend, continuing build..."
exit 1